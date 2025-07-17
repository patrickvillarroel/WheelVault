package io.github.patrickvillarroel.wheel.vault.ui.screen

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class CarViewModel(private val carsRepository: CarsRepository) : ViewModel() {
    private val _cars = MutableStateFlow<CarsUiState>(CarsUiState.Loading)
    val cars = _cars.asStateFlow()
    private val _recentCarsImages = MutableStateFlow<List<Pair<UUID, Any>>>(emptyList())
    val recentCarsImages = _recentCarsImages.asStateFlow()

    fun fetchRecentCars() {
        if (_recentCarsImages.value.isEmpty()) {
            viewModelScope.launch {
                try {
                    val result = carsRepository.fetchAll()
                    _recentCarsImages.update {
                        result.map { it.id to it.images.first() }
                    }
                } catch (e: Exception) {
                    Log.e("CarViewModel", "Failed to fetch recent cars", e)
                    _cars.update { CarsUiState.Error }
                }
            }
        }
    }

    sealed interface CarsUiState {
        object Loading : CarsUiState

        @Immutable
        data class Success(@Stable val cars: List<CarItem>) : CarsUiState
        data object Error : CarsUiState
    }
}
