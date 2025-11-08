package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.data.paging.asPagingSource
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GarageViewModel(private val carsRepository: CarsRepository) : ViewModel() {
    companion object {
        private val logger = Logger.withTag("GarageViewModel")
    }

    private val _garageState = MutableStateFlow<GarageUiState>(GarageUiState.Loading)
    val garageState = _garageState.asStateFlow()
    val carsPaged = Pager(config = PagingConfig(10, initialLoadSize = 10)) {
        carsRepository.fetchAllPaged().asPagingSource()
    }.flow.cachedIn(viewModelScope)

    fun fetchAll(force: Boolean = false, orderAsc: Boolean = false) {
        val shouldFetch = force ||
            garageState.value is GarageUiState.Error ||
            garageState.value is GarageUiState.Loading

        if (!shouldFetch) {
            // TODO remove this when cars repositor impl have cache/local fetch internally
            logger.d { "No fetching cars on garage" }
            return
        }

        _garageState.update { GarageUiState.Loading }
        viewModelScope.launch {
            try {
                val result = carsRepository.fetchAll(limit = 100, orderAsc = orderAsc)
                if (result.isEmpty()) {
                    _garageState.update { GarageUiState.Empty }
                } else {
                    _garageState.update { GarageUiState.Success(result) }
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Failed to fetch cars", e)
                _garageState.update { GarageUiState.Error }
            }
        }
    }

    fun search(query: String, favoritesOnly: Boolean = false) {
        _garageState.update { GarageUiState.Loading }
        viewModelScope.launch {
            try {
                val result = carsRepository.search(query, favoritesOnly)
                if (result.isEmpty()) {
                    _garageState.update { GarageUiState.Empty }
                } else {
                    _garageState.update { GarageUiState.Success(result) }
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Failed to search cars", e)
                _garageState.update { GarageUiState.Error }
            }
        }
    }

    fun fetchFavorites() {
        _garageState.update { GarageUiState.Loading }
        viewModelScope.launch {
            try {
                val result = carsRepository.fetchAll(true, 25)
                if (result.isEmpty()) {
                    _garageState.update { GarageUiState.Empty }
                } else {
                    _garageState.update { GarageUiState.Success(result) }
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Failed to get favorites", e)
                _garageState.update { GarageUiState.Error }
            }
        }
    }

    fun filterByManufacturer(manufacturer: String) {
        _garageState.update { GarageUiState.Loading }
        viewModelScope.launch {
            try {
                val result = carsRepository.fetchByManufacturer(manufacturer)
                if (result.isEmpty()) {
                    _garageState.update { GarageUiState.Empty }
                } else {
                    _garageState.update { GarageUiState.Success(result) }
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Failed to filter by brand", e)
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
