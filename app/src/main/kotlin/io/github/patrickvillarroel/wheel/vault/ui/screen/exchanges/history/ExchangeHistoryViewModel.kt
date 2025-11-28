package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.history

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

class ExchangeHistoryViewModel(
    private val tradeRepository: TradeRepository,
) : ViewModel() {
    companion object {
        private val logger = Logger.withTag("ExchangeHistoryViewModel")
    }

    private val _historyState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val historyState = _historyState.asStateFlow()

    fun loadHistory() {
        _historyState.update { HistoryUiState.Loading }
        viewModelScope.launch {
            try {
                val activeTrades = tradeRepository.getActiveTrades()

                // Filtrar solo los intercambios completados o finalizados
                val completedTrades = activeTrades.filter { trade ->
                    !trade.isActive || trade.isSuccessful
                }

                _historyState.update {
                    HistoryUiState.Success(completedTrades = completedTrades)
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Error loading exchange history", e)
                _historyState.update { HistoryUiState.Error }
            }
        }
    }

    sealed interface HistoryUiState {
        data object Loading : HistoryUiState

        @Immutable
        data class Success(
            val completedTrades: List<TradeProposal.CurrentTradeStatus>,
        ) : HistoryUiState

        data object Error : HistoryUiState
    }
}
