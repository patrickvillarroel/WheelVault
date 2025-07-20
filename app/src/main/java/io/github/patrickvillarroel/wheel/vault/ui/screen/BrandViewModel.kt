package io.github.patrickvillarroel.wheel.vault.ui.screen

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class BrandViewModel(
    private val brandRepository: BrandRepository,
    private val carRepository: CarsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _brandsState = MutableStateFlow<BrandsUiState>(BrandsUiState.Loading)
    val brandsState = _brandsState.asStateFlow()

    private val _brandDetailsState = MutableStateFlow<BrandDetailsUiState>(BrandDetailsUiState.Idle)
    val brandDetailsState = _brandDetailsState.asStateFlow()

    val brandsImages = brandsState.map { state ->
        if (state is BrandsUiState.Success) {
            state.brands.map { it.id to it.image }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val brandsNames = brandsState.map { state ->
        if (state is BrandsUiState.Success) {
            state.brands.map { it.name }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        fetchAll()
    }

    fun fetchAll(force: Boolean = false) {
        val shouldFetch = force ||
            brandsImages.value.isEmpty() ||
            brandsImages.value.size == 1 ||
            brandsState.value is BrandsUiState.Error ||
            brandsState.value is BrandsUiState.Loading

        if (shouldFetch) {
            _brandsState.update { BrandsUiState.Loading }
            viewModelScope.launch(ioDispatcher) {
                Log.i("Brands", "Fetching all brands")
                try {
                    val result = brandRepository.fetchAll()
                    _brandsState.update { BrandsUiState.Success(result) }
                } catch (e: Exception) {
                    Log.e("Brands", "Error fetch all", e)
                    _brandsState.update { BrandsUiState.Error }
                }
            }
        }
    }

    fun findById(id: UUID) {
        val localBrand = (brandsState.value as? BrandsUiState.Success)
            ?.brands
            ?.firstOrNull { it.id == id }

        if (localBrand != null) {
            val localCars = (brandDetailsState.value as? BrandDetailsUiState.Success)
                ?.takeIf { it.brand.id == id }
                ?.cars

            if (localCars != null) {
                _brandDetailsState.update { BrandDetailsUiState.Success(localBrand, localCars) }
                return
            }
        }

        _brandDetailsState.update { BrandDetailsUiState.Loading }
        viewModelScope.launch(ioDispatcher) {
            Log.i("Brands", "Fetching brand by id $id")

            try {
                val brand = brandRepository.fetch(id)
                if (brand != null) {
                    val cars = carRepository.fetchByBrand(brand.name)
                    _brandDetailsState.update { BrandDetailsUiState.Success(brand, cars) }
                } else {
                    _brandDetailsState.update { BrandDetailsUiState.NotFound }
                }
            } catch (e: Exception) {
                Log.e("Brands", "Error fetching brand by id $id", e)
                _brandDetailsState.update { BrandDetailsUiState.Error }
            }
        }
    }

    sealed interface BrandsUiState {
        data object Loading : BrandsUiState

        @Immutable
        data class Success(@Stable val brands: List<Brand>) : BrandsUiState
        data object Error : BrandsUiState
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
        val manufacturerList = listOf("HotWheels", "MiniGT", "Maisto", "Bburago", "Matchbox").sorted()
    }
}
