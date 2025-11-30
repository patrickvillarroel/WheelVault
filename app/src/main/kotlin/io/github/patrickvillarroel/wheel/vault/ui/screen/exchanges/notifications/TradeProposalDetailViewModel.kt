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

class TradeProposalDetailViewModel(
    private val tradeRepository: TradeRepository,
    private val carsRepository: CarsRepository,
    private val supabaseClient: SupabaseClient,
) : ViewModel() {
    companion object {
        private val logger = Logger.withTag("TradeProposalDetailViewModel")
    }

    private val _uiState = MutableStateFlow<TradeDetailUiState>(TradeDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadTradeProposal(tradeGroupId: Uuid) {
        _uiState.update { TradeDetailUiState.Loading }
        viewModelScope.launch {
            try {
                val currentUserId = getCurrentUserId()

                val activeTrades = tradeRepository.getActiveTrades()
                val trade = activeTrades.firstOrNull { it.tradeGroupId == tradeGroupId }
                    ?: throw IllegalStateException("Trade not found")

                val offeredCar = carsRepository.fetch(trade.offeredCarId)
                    ?: throw IllegalStateException("Offered car not found")

                val requestedCar = carsRepository.fetch(trade.requestedCarId)
                    ?: throw IllegalStateException("Requested car not found")

                val isReceived = trade.ownerId.toString() == currentUserId

                _uiState.update {
                    TradeDetailUiState.Success(
                        trade = trade,
                        offeredCar = offeredCar,
                        requestedCar = requestedCar,
                        isReceived = isReceived,
                    )
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Error loading trade proposal", e)
                _uiState.update { TradeDetailUiState.Error }
            }
        }
    }

    fun acceptTrade(tradeGroupId: Uuid) {
        _uiState.update { TradeDetailUiState.Loading }
        viewModelScope.launch {
            try {
                tradeRepository.respondToTrade(
                    tradeGroupId = tradeGroupId,
                    accept = true,
                    responseMessage = "Aceptada por el usuario",
                )
                _uiState.update { TradeDetailUiState.ActionCompleted(TradeAction.ACCEPTED) }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Error accepting trade", e)
                _uiState.update { TradeDetailUiState.Error }
            }
        }
    }

    fun rejectTrade(tradeGroupId: Uuid) {
        _uiState.update { TradeDetailUiState.Loading }
        viewModelScope.launch {
            try {
                tradeRepository.respondToTrade(
                    tradeGroupId = tradeGroupId,
                    accept = false,
                    responseMessage = "Rechazada por el usuario",
                )
                _uiState.update { TradeDetailUiState.ActionCompleted(TradeAction.REJECTED) }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Error rejecting trade", e)
                _uiState.update { TradeDetailUiState.Error }
            }
        }
    }

    private fun getCurrentUserId(): String {
        return supabaseClient.auth.currentUserOrNull()?.id ?: ""
    }

    enum class TradeAction {
        ACCEPTED,
        REJECTED
    }

    sealed interface TradeDetailUiState {
        data object Loading : TradeDetailUiState

        @Immutable
        data class Success(
            @Stable val trade: TradeProposal.CurrentTradeStatus,
            @Stable val offeredCar: CarItem,
            @Stable val requestedCar: CarItem,
            val isReceived: Boolean,
        ) : TradeDetailUiState

        @Immutable
        data class ActionCompleted(val action: TradeAction) : TradeDetailUiState

        data object Error : TradeDetailUiState
    }
}
