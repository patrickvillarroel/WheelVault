package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.selection

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExchangeCarSelectionViewModel(
    private val carsRepository: CarsRepository,
    private val tradeRepository: io.github.patrickvillarroel.wheel.vault.domain.repository.TradeRepository,
) : ViewModel() {
    companion object {
        private val logger = Logger.withTag("ExchangeCarVM")
    }
    private val _carsState = MutableStateFlow<CarsUiState>(CarsUiState.Loading)
    val carsState = _carsState.asStateFlow()

    fun fetchAll(force: Boolean = false) {
        val shouldFetch = force ||
            carsState.value is CarsUiState.Error ||
            carsState.value is CarsUiState.Loading

        if (shouldFetch) {
            _carsState.update { CarsUiState.Loading }
            viewModelScope.launch {
                try {
                    // Obtener los autos del USUARIO para que seleccione cuál ofrecer
                    val allCars = carsRepository.fetchAll(orderAsc = false)

                    // Obtener IDs de carros que ya están en solicitudes activas
                    val offeredCarsInActiveTrades = tradeRepository.getMyOfferedCarsInActiveTrades()

                    logger.d { "Loaded ${allCars.size} user's cars, ${offeredCarsInActiveTrades.size} already in active trades" }

                    // Separar carros disponibles y no disponibles
                    val availableCars = allCars.filter { it.id !in offeredCarsInActiveTrades }
                    val unavailableCars = allCars.filter { it.id in offeredCarsInActiveTrades }

                    _carsState.update {
                        CarsUiState.Success(
                            availableCars = availableCars,
                            carsInActiveTrades = unavailableCars
                        )
                    }
                } catch (e: Exception) {
                    logger.e(e) { "Failed to fetch user's cars" }
                    _carsState.update { CarsUiState.Error }
                }
            }
        }
    }

    sealed interface CarsUiState {
        data object Loading : CarsUiState

        @Immutable
        data class Success(
            @Stable val availableCars: List<CarItem>,
            @Stable val carsInActiveTrades: List<CarItem> = emptyList()
        ) : CarsUiState

        data object Error : CarsUiState
    }
}
