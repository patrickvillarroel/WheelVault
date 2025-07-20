package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GarageViewModel(
    private val carsRepository: CarsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private val _garageState = MutableStateFlow<GarageUiState>(GarageUiState.Loading)
    val garageState = _garageState.asStateFlow()

    init {
        fetchAll()
    }

    fun fetchAll(force: Boolean = false, orderAsc: Boolean = false) {
        val shouldFetch = force ||
            garageState.value is GarageUiState.Error ||
            garageState.value is GarageUiState.Loading

        if (shouldFetch) {
            _garageState.update { GarageUiState.Loading }
            viewModelScope.launch(ioDispatcher) {
                try {
                    val result = carsRepository.fetchAll(orderAsc = orderAsc)
                    if (result.isEmpty()) {
                        _garageState.update { GarageUiState.Empty }
                    } else {
                        _garageState.update { GarageUiState.Success(result) }
                    }
                } catch (e: Exception) {
                    Log.e("CarViewModel", "Failed to fetch cars", e)
                    _garageState.update { GarageUiState.Error }
                }
            }
        }
    }

    fun search(query: String, favoritesOnly: Boolean = false) {
        viewModelScope.launch(ioDispatcher) {
            try {
                val result = carsRepository.search(query, favoritesOnly)
                if (result.isEmpty()) {
                    _garageState.update { GarageUiState.Empty }
                } else {
                    _garageState.update { GarageUiState.Success(result) }
                }
            } catch (e: Exception) {
                Log.e("CarViewModel", "Failed to search cars", e)
                _garageState.update { GarageUiState.Error }
            }
        }
    }

    fun fetchFavorites() {
        viewModelScope.launch(ioDispatcher) {
            try {
                _garageState.update { GarageUiState.Loading }
                val result = carsRepository.fetchAll(true, 25)
                if (result.isEmpty()) {
                    _garageState.update { GarageUiState.Empty }
                } else {
                    _garageState.update { GarageUiState.Success(result) }
                }
            } catch (e: Exception) {
                Log.e("CarViewModel", "Failed to get favorites", e)
                _garageState.update { GarageUiState.Error }
            }
        }
    }

    fun filterByManufacturer(manufacturer: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
                _garageState.update { GarageUiState.Loading }
                val result = carsRepository.fetchByManufacturer(manufacturer)
                _garageState.update { GarageUiState.Success(result) }
            } catch (e: Exception) {
                Log.e("CarViewModel", "Failed to filter by brand", e)
                _garageState.update { GarageUiState.Error }
            }
        }
    }

    sealed interface GarageUiState {
        data object Loading : GarageUiState

        @Immutable
        data class Success(@Stable val cars: List<CarItem>) : GarageUiState

        data object Error : GarageUiState

        data object Empty : GarageUiState
    }
}
