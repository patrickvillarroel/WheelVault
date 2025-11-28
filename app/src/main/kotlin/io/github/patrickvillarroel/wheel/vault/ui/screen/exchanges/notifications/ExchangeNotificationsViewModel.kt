package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.notifications

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.domain.model.TradeProposal
import io.github.patrickvillarroel.wheel.vault.domain.repository.TradeRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExchangeNotificationsViewModel(
    private val tradeRepository: TradeRepository,
) : ViewModel() {
    companion object {
        private val logger = Logger.withTag("ExchangeNotificationsViewModel")
    }

    private val _notificationsState = MutableStateFlow<NotificationsUiState>(NotificationsUiState.Loading)
    val notificationsState = _notificationsState.asStateFlow()

    fun loadNotifications() {
        _notificationsState.update { NotificationsUiState.Loading }
        viewModelScope.launch {
            try {
                val activeTrades = tradeRepository.getActiveTrades()

                // Separar entre propuestas recibidas y enviadas
                // Asumiendo que el usuario actual es el owner cuando recibe propuestas
                // y es el requester cuando las envía
                val currentUserId = getCurrentUserId() // Necesitarás implementar esto

                val receivedProposals = activeTrades.filter { trade ->
                    trade.ownerId.toString() == currentUserId && trade.currentStatus == TradeProposal.TradeEventType.PROPOSED
                }
                val sentProposals = activeTrades.filter { trade ->
                    trade.requesterId.toString() == currentUserId && trade.currentStatus == TradeProposal.TradeEventType.PROPOSED
                }

                _notificationsState.update {
                    NotificationsUiState.Success(
                        receivedProposals = receivedProposals,
                        sentProposals = sentProposals,
                    )
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Error loading notifications", e)
                _notificationsState.update { NotificationsUiState.Error }
            }
        }
    }

    private fun getCurrentUserId(): String {
        // TODO: Obtener el ID del usuario actual desde el repositorio de autenticación
        return ""
    }

    sealed interface NotificationsUiState {
        data object Loading : NotificationsUiState

        @Immutable
        data class Success(
            val receivedProposals: List<TradeProposal.CurrentTradeStatus>,
            val sentProposals: List<TradeProposal.CurrentTradeStatus>,
        ) : NotificationsUiState

        data object Error : NotificationsUiState
    }
}
