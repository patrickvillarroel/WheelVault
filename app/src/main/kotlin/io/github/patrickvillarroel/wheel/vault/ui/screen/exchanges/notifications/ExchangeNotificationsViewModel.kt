package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.notifications

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.model.TradeProposal
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.TradeRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class ExchangeNotificationsViewModel(
    private val tradeRepository: TradeRepository,
    private val carsRepository: CarsRepository,
    private val supabaseClient: SupabaseClient,
) : ViewModel() {
    companion object {
        private val logger = Logger.withTag("ExchangeNotificationsViewModel")
    }

    private val _notificationsState = MutableStateFlow<NotificationsUiState>(NotificationsUiState.Loading)
    val notificationsState = _notificationsState.asStateFlow()

    fun loadNotifications(forceRefresh: Boolean = false) {
        // No recargar si ya tenemos datos y no es refresh forzado
        if (!forceRefresh && _notificationsState.value is NotificationsUiState.Success) {
            logger.d { "Skipping reload - data already loaded" }
            return
        }

        _notificationsState.update { NotificationsUiState.Loading }
        viewModelScope.launch {
            try {
                val activeTrades = tradeRepository.getActiveTrades()
                val currentUserId = getCurrentUserId()

                logger.d { "Loading notifications for user: $currentUserId, total trades: ${activeTrades.size}" }

                // Separar entre propuestas recibidas y enviadas
                val receivedProposals = activeTrades.filter { trade ->
                    trade.ownerId.toString() == currentUserId
                }
                val sentProposals = activeTrades.filter { trade ->
                    trade.requesterId.toString() == currentUserId
                }

                logger.d { "Received: ${receivedProposals.size}, Sent: ${sentProposals.size}" }

                // Cache de carros para evitar cargar el mismo carro múltiples veces
                val carsCache = mutableMapOf<Uuid, CarItem?>()

                suspend fun getCar(carId: Uuid): CarItem? = carsCache.getOrPut(carId) {
                    try {
                        carsRepository.fetch(carId)
                    } catch (e: Exception) {
                        logger.w { "Failed to load car $carId: ${e.message}" }
                        null
                    }
                }

                // Mapear trades con sus carros usando cache
                val receivedWithCars = receivedProposals.mapNotNull { trade ->
                    val offeredCar = getCar(trade.offeredCarId)
                    val requestedCar = getCar(trade.requestedCarId)

                    if (offeredCar != null && requestedCar != null) {
                        TradeNotification(
                            trade = trade,
                            offeredCar = offeredCar,
                            requestedCar = requestedCar,
                        )
                    } else {
                        logger.w { "Missing cars for trade ${trade.tradeGroupId}" }
                        null
                    }
                }

                // FIXME separate loading of send and received
                val sentWithCars = sentProposals.mapNotNull { trade ->
                    val offeredCar = getCar(trade.offeredCarId)
                    val requestedCar = getCar(trade.requestedCarId)

                    if (offeredCar != null && requestedCar != null) {
                        TradeNotification(
                            trade = trade,
                            offeredCar = offeredCar,
                            requestedCar = requestedCar,
                        )
                    } else {
                        logger.w { "Missing cars for trade ${trade.tradeGroupId}" }
                        null
                    }
                }

                logger.d {
                    "Loaded ${carsCache.size} unique cars (${carsCache.values.count { it != null }} successful)"
                }

                _notificationsState.update {
                    NotificationsUiState.Success(
                        receivedProposals = receivedWithCars,
                        sentProposals = sentWithCars,
                    )
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Error loading notifications", e)
                _notificationsState.update { NotificationsUiState.Error }
            }
        }
    }

    private fun getCurrentUserId(): String = supabaseClient.auth.currentUserOrNull()?.id ?: ""

    @Immutable
    data class TradeNotification(
        @Stable val trade: TradeProposal.CurrentTradeStatus,
        @Stable val offeredCar: CarItem,
        @Stable val requestedCar: CarItem,
    )

    sealed interface NotificationsUiState {
        data object Loading : NotificationsUiState

        @Immutable
        data class Success(
            val receivedProposals: List<TradeNotification>,
            val sentProposals: List<TradeNotification>,
        ) : NotificationsUiState

        data object Error : NotificationsUiState
    }
}
