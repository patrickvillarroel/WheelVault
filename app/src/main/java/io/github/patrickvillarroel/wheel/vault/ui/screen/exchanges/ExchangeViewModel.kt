package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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

    sealed interface ExchangeUiState {
        object Loading : ExchangeUiState

        @Immutable
        class Success(@Stable val cars: List<CarItem>) : ExchangeUiState
        object Error : ExchangeUiState
    }
}
