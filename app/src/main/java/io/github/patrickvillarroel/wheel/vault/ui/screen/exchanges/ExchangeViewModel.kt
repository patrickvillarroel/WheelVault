package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ExchangeViewModel(private val tradeRepository: TradeRepository) : ViewModel() {
    private val _exchangeUiState = MutableStateFlow<ExchangeUiState>(ExchangeUiState.Loading)
    val exchangeState = _exchangeUiState.asStateFlow()

    private val _exchangeConfirmState = MutableStateFlow<ExchangeConfirmUiState>(ExchangeConfirmUiState.Loading)
    val exchangeConfirmState = _exchangeConfirmState.asStateFlow()

    fun exchangeCar(carItem: CarItem) {
        /* TODO */
    }

    fun offersOf(carId: UUID) {
        /* TODO */
    }

    fun rejectExchange(offeredCar: CarItem, requestedCar: CarItem) {
        /* TODO */
    }

    fun acceptExchange(offeredCar: CarItem, requestedCar: CarItem) {
        /* TODO */
    }

    sealed interface ExchangeUiState {
        object Loading : ExchangeUiState

        @Immutable
        class Success(@Stable val cars: List<CarItem>) : ExchangeUiState
        object Error : ExchangeUiState
    }

    sealed interface ExchangeConfirmUiState {
        object Loading : ExchangeConfirmUiState

        @Immutable
        class WaitingConfirm(val offeredCar: CarItem, val requestedCar: CarItem) : ExchangeConfirmUiState
        object Accepted : ExchangeConfirmUiState
        object Rejected : ExchangeConfirmUiState
        object Error : ExchangeConfirmUiState
    }
}
