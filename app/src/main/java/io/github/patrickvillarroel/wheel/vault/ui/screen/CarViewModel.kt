package io.github.patrickvillarroel.wheel.vault.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.Uri
import coil3.toAndroidUri
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.github.patrickvillarroel.wheel.vault.util.bitmapToByteArray
import io.github.patrickvillarroel.wheel.vault.util.uriToByteArray
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

data class CarViewModel(
    private val carsRepository: CarsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _carsState = MutableStateFlow<CarsUiState>(CarsUiState.Loading)
    val carsState = _carsState.asStateFlow()

    private val _carDetailState = MutableStateFlow<CarDetailUiState>(CarDetailUiState.Idle)
    val carDetailState = _carDetailState.asStateFlow()

    val recentCarsImages = carsState.map { state ->
        if (state is CarsUiState.Success) {
            state.cars.mapNotNull { car -> car.images.firstOrNull()?.let { car.id to it } }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        fetchAll()
    }

    fun fetchAll(force: Boolean = false) {
        val shouldFetch = force ||
            recentCarsImages.value.isEmpty() ||
            recentCarsImages.value.size == 1 ||
            carsState.value is CarsUiState.Error ||
            carsState.value is CarsUiState.Loading

        if (shouldFetch) {
            _carsState.update { CarsUiState.Loading }
            viewModelScope.launch(ioDispatcher) {
                try {
                    val result = carsRepository.fetchAll()
                    _carsState.update { CarsUiState.Success(result) }
                } catch (e: Exception) {
                    Log.e("CarViewModel", "Failed to fetch cars", e)
                    _carsState.update { CarsUiState.Error }
                }
            }
        }
    }

    fun findById(id: UUID, force: Boolean = false) {
        val localMatch = (carsState.value as? CarsUiState.Success)
            ?.cars
            ?.firstOrNull { it.id == id }

        if (localMatch != null && !force) {
            Log.i("CarViewModel", "Found car in local state")
            _carDetailState.update { CarDetailUiState.Success(localMatch) }
            return
        }

        _carDetailState.update { CarDetailUiState.Loading }

        viewModelScope.launch(ioDispatcher) {
            try {
                Log.i("CarViewModel", "Going to find car by id $id")
                val car = carsRepository.fetch(id)
                if (car != null) {
                    Log.i("CarViewModel", "Found car by id $id")
                    _carDetailState.update { CarDetailUiState.Success(car) }
                } else {
                    Log.i("CarViewModel", "Car not found by id $id")
                    _carDetailState.update { CarDetailUiState.NotFound }
                }
            } catch (e: Exception) {
                Log.e("CarViewModel", "Failed to find car by id", e)
                _carDetailState.update { CarDetailUiState.Error }
            }
        }
    }

    fun save(car: CarItem.Partial, context: Context? = null) {
        viewModelScope.launch(ioDispatcher) {
            Log.i("Car VM", "Going to save this car $car")
            val built = car.toCarItem() ?: return@launch
            Log.i("Car VM", "Convert as item: $built")
            save(built, context)
        }
    }

    fun save(car: CarItem, context: Context? = null) {
        viewModelScope.launch(ioDispatcher) {
            Log.i("Car VM", "Going to save this car $car")
            try {
                val pictures = car.images.mapNotNull {
                    when (it) {
                        is Bitmap -> bitmapToByteArray(it)

                        is Uri -> {
                            if (context == null) {
                                Log.e("Car VM", "Context is null and image is a uri")
                                return@mapNotNull null
                            }
                            uriToByteArray(context, it.toAndroidUri())
                        }

                        else -> null
                    }
                }
                    .toSet()
                    .ifEmpty { setOfNotNull(CarItem.EmptyImage) }
                val car = car.copy(images = pictures, imageUrl = pictures.first())
                Log.i("Car VM", "Final car: $car")
                val newCarState = if (carsRepository.exist(car.id)) {
                    Log.i("Car VM", "The car exist")
                    carsRepository.update(car)
                } else {
                    carsRepository.insert(car)
                }
                findById(newCarState.id, true)
            } catch (e: Exception) {
                Log.e("Car VM", "Failed to save car", e)
                fetchAll(true)
            }
        }
    }

    fun delete(car: CarItem) {
        viewModelScope.launch(ioDispatcher) {
            carsRepository.delete(car)
            fetchAll(true)
            if ((_carDetailState.value as? CarDetailUiState.Success)?.car?.id == car.id) {
                _carDetailState.update { CarDetailUiState.Idle }
            }
        }
    }

    sealed interface CarsUiState {
        data object Loading : CarsUiState

        @Immutable
        data class Success(@Stable val cars: List<CarItem>) : CarsUiState

        data object Error : CarsUiState
    }

    sealed interface CarDetailUiState {
        data object Idle : CarDetailUiState
        data object Loading : CarDetailUiState
        data object NotFound : CarDetailUiState

        @Immutable
        data class Success(@Stable val car: CarItem) : CarDetailUiState
        data object Error : CarDetailUiState
    }
}
