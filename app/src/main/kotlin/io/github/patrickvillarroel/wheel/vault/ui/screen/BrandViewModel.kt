package io.github.patrickvillarroel.wheel.vault.ui.screen

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class BrandViewModel(private val brandRepository: BrandRepository, private val carRepository: CarsRepository) :
    ViewModel() {
    private val _brandDetailsState = MutableStateFlow<BrandDetailsUiState>(BrandDetailsUiState.Idle)
    val brandDetailsState = _brandDetailsState.asStateFlow()
    private val _brandsNames = MutableStateFlow(emptyList<String>())

    val brandsNames = _brandsNames.asStateFlow()

    fun fetchNames(force: Boolean = false) {
        viewModelScope.launch {
            logger.d { "Fetching brand names..." }
            try {
                val names = brandRepository.fetchAllNames(force)
                logger.i { "Found ${names.size} brand names" }
                _brandsNames.update { names }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e(e) { "Unexpected error while fetching brand names" }
                // TODO update UI state with this error or something
            }
        }
    }

    fun findById(id: Uuid) {
        _brandDetailsState.update { BrandDetailsUiState.Loading }
        logger.d { "Fetching brand by id $id" }
        viewModelScope.launch {
            try {
                val brand = brandRepository.fetch(id)
                if (brand != null) {
                    val cars = carRepository.fetchByManufacturer(brand.name)
                    _brandDetailsState.update { BrandDetailsUiState.Success(brand, cars) }
                } else {
                    _brandDetailsState.update { BrandDetailsUiState.NotFound }
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e(e) { "Error fetching brand by id $id" }
                _brandDetailsState.update { BrandDetailsUiState.Error }
            }
        }
    }

    sealed interface BrandDetailsUiState {
        data object Idle : BrandDetailsUiState
        data object Loading : BrandDetailsUiState

        @Immutable
        data class Success(@Stable val brand: Brand, @Stable val cars: List<CarItem>) : BrandDetailsUiState
        data object NotFound : BrandDetailsUiState
        data object Error : BrandDetailsUiState
    }

    companion object {
        private val logger = Logger.withTag("BrandsViewModel")

        @VisibleForTesting
        val manufacturerList = listOf("HotWheels", "MiniGT", "Maisto", "Bburago", "Matchbox").sorted()
    }
}
