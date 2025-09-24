package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.UUID

/* Temporal TODO change datasource of data */
class ExchangeViewModel(garageViewModel: GarageViewModel) : ViewModel() {
    val exchangeState = garageViewModel.garageState.map {
        when (it) {
            GarageViewModel.GarageUiState.Empty -> ExchangeUiState.Success(emptyList())
            GarageViewModel.GarageUiState.Error -> ExchangeUiState.Error
            GarageViewModel.GarageUiState.Loading -> ExchangeUiState.Loading
            is GarageViewModel.GarageUiState.Success -> ExchangeUiState.Success(it.cars)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExchangeUiState.Loading)
    private val _exchangeConfirmState = MutableStateFlow<ExchangeConfirmUiState>(ExchangeConfirmUiState.Loading)
    val exchangeConfirmState = _exchangeConfirmState.asStateFlow()

    fun exchangeCar(carItem: CarItem) {
        /* TODO */
    }

    fun offersOf(carId: UUID) {
        _exchangeConfirmState.update {
            ExchangeConfirmUiState.WaitingConfirm(
                (exchangeState.value as ExchangeUiState.Success).cars.first { it.id == carId },
                (exchangeState.value as ExchangeUiState.Success).cars.random(),
            )
        }
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
