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
    private val _carsState = MutableStateFlow<CarsUiState>(CarsUiState.Loading)
    val carsState = _carsState.asStateFlow()

    init {
        fetchAll()
    }

    fun fetchAll(force: Boolean = false, orderAsc: Boolean = false) {
        val shouldFetch = force ||
            carsState.value is CarsUiState.Error ||
            carsState.value is CarsUiState.Loading

        if (shouldFetch) {
            _carsState.update { CarsUiState.Loading }
            viewModelScope.launch(ioDispatcher) {
                try {
                    val result = carsRepository.fetchAll(orderAsc = orderAsc)
                    _carsState.update { CarsUiState.Success(result) }
                } catch (e: Exception) {
                    Log.e("CarViewModel", "Failed to fetch cars", e)
                    _carsState.update { CarsUiState.Error }
                }
            }
        }
    }

    fun search(query: String, favoritesOnly: Boolean = false) {
        viewModelScope.launch(ioDispatcher) {
            try {
                val result = carsRepository.search(query, favoritesOnly)
                _carsState.update { CarsUiState.Success(result) }
            } catch (e: Exception) {
                Log.e("CarViewModel", "Failed to search cars", e)
                _carsState.update { CarsUiState.Error }
            }
        }
    }

    fun fetchFavorites() {
        viewModelScope.launch(ioDispatcher) {
            try {
                _carsState.update { CarsUiState.Loading }
                val result = carsRepository.fetchAll(true, 25)
                _carsState.update { CarsUiState.Success(result) }
            } catch (e: Exception) {
                Log.e("CarViewModel", "Failed to get favorites", e)
                _carsState.update { CarsUiState.Error }
            }
        }
    }

    fun filterByBrand(brand: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
                _carsState.update { CarsUiState.Loading }
                val result = carsRepository.fetchByBrand(brand)
                _carsState.update { CarsUiState.Success(result) }
            } catch (e: Exception) {
                Log.e("CarViewModel", "Failed to filter by brand", e)
                _carsState.update { CarsUiState.Error }
            }
        }
    }

    sealed interface CarsUiState {
        data object Loading : CarsUiState

        @Immutable
        data class Success(@Stable val cars: List<CarItem>) : CarsUiState

        data object Error : CarsUiState
    }
}
