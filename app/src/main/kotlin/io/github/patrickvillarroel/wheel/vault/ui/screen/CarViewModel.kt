package io.github.patrickvillarroel.wheel.vault.ui.screen

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import coil3.toAndroidUri
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.github.patrickvillarroel.wheel.vault.util.toByteArray
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid
import android.content.Context as AndroidContext
import coil3.Uri as Coil3Uri

class CarViewModel(
    private val carsRepository: CarsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private val logger = Logger.withTag("Car VM")
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
                    val result = carsRepository.fetchAll(orderAsc = false)
                    _carsState.update { CarsUiState.Success(result) }
                } catch (e: Exception) {
                    logger.e(e) { "Failed to fetch cars" }
                    _carsState.update { CarsUiState.Error }
                }
            }
        }
    }

    fun findById(id: Uuid, force: Boolean = false) {
        val localMatch = (carsState.value as? CarsUiState.Success)
            ?.cars
            ?.firstOrNull { it.id == id }

        if (localMatch != null && !force) {
            logger.i { "Found car in local state. $localMatch" }
            _carDetailState.update { CarDetailUiState.Success(localMatch) }
            return
        }

        _carDetailState.update { CarDetailUiState.Loading }

        viewModelScope.launch(ioDispatcher) {
            try {
                logger.i { "Going to find car by id $id" }
                val car = carsRepository.fetch(id)
                if (car != null) {
                    logger.i { "Found car by id $id. $car" }
                    _carDetailState.update { CarDetailUiState.Success(car) }
                } else {
                    logger.i { "Car not found by id $id" }
                    _carDetailState.update { CarDetailUiState.NotFound }
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e(e) { "Failed to find car by id" }
                _carDetailState.update { CarDetailUiState.Error }
            }
        }
    }

    fun save(car: CarItem.Builder, context: AndroidContext? = null) {
        logger.v { "Going to save this car $car" }
        _carDetailState.update { CarDetailUiState.Loading }
        val built = car.build() ?: run {
            logger.e(
                "Failed to build car - Builder data: model=${car.model}, " +
                    "year=${car.year}, brand=${car.brand}, manufacturer=${car.manufacturer}",
            )
            _carDetailState.update { CarDetailUiState.Error }
            return
        }
        logger.v { "Convert as item: $built" }
        save(built, context)
    }

    fun save(car: CarItem, context: AndroidContext? = null) {
        logger.i("Going to save this car $car")
        _carDetailState.update { CarDetailUiState.Loading }
        try {
            val pictures = car.images.mapNotNull {
                when (it) {
                    is Bitmap -> it.toByteArray()

                    is Coil3Uri -> {
                        if (context == null) {
                            logger.e("Context is null and image is a uri")
                            return@mapNotNull null
                        }
                        it.toAndroidUri().toByteArray(context)
                    }

                    else -> null
                }
            }
                .toSet()

            // If no real images, use empty set; the backend will handle the default image
            val imagesToSave = pictures.ifEmpty { emptySet() }
            val carToSave = car.copy(
                images = imagesToSave.ifEmpty { setOfNotNull(CarItem.EmptyImage) },
                imageUrl = imagesToSave.firstOrNull() ?: CarItem.EmptyImage,
            )
            logger.i("Final car: $carToSave")
            viewModelScope.launch(ioDispatcher) {
                try {
                    val newCarState = if (carsRepository.exist(carToSave.id)) {
                        logger.i("The car exist")
                        carsRepository.update(carToSave)
                    } else {
                        carsRepository.insert(carToSave)
                    }
                    logger.i("New car state: $newCarState")
                    _carDetailState.update { CarDetailUiState.Success(newCarState) }

                    // Update the car in the main list as well
                    _carsState.update { currentState ->
                        if (currentState is CarsUiState.Success) {
                            val carExists = currentState.cars.any { it.id == newCarState.id }
                            val updatedCars = if (carExists) {
                                // Update existing car
                                currentState.cars.map {
                                    if (it.id == newCarState.id) newCarState else it
                                }
                            } else {
                                // Add new car to the beginning of the list
                                listOf(newCarState) + currentState.cars
                            }
                            CarsUiState.Success(updatedCars)
                        } else {
                            currentState
                        }
                    }
                } catch (e: Exception) {
                    currentCoroutineContext().ensureActive()
                    logger.e("Failed to save car in database", e)
                    _carDetailState.update { CarDetailUiState.Error }
                }
            }
        } catch (e: Exception) {
            logger.e("Failed to save car (image processing)", e)
            // Potentially set an error state for carDetailState or carsState
            _carDetailState.update { CarDetailUiState.Error } // Example error handling
        }
    }

    fun delete(car: CarItem) {
        viewModelScope.launch(ioDispatcher) {
            try {
                if (!carsRepository.delete(car)) {
                    logger.e("Failed to delete car")
                    _carDetailState.update { CarDetailUiState.Error }
                    return@launch
                }
                // Update main list
                _carsState.update { currentState ->
                    if (currentState is CarsUiState.Success) {
                        CarsUiState.Success(currentState.cars.filterNot { it.id == car.id })
                    } else {
                        currentState
                    }
                }
                // Update detail view if it was the deleted car
                if ((_carDetailState.value as? CarDetailUiState.Success)?.car?.id == car.id) {
                    _carDetailState.update { CarDetailUiState.Idle }
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Failed to delete car", e)
                _carDetailState.update { CarDetailUiState.Error }
            }
        }
    }

    fun toggleCarTradeAvailability(carToUpdate: CarItem) {
        val originalDetailState = _carDetailState.value
        // Optimistically update the detail state if it's the current car
        if (originalDetailState is CarDetailUiState.Success && originalDetailState.car.id == carToUpdate.id) {
            _carDetailState.update {
                CarDetailUiState.Success(carToUpdate.copy(availableForTrade = !carToUpdate.availableForTrade))
            }
        }

        viewModelScope.launch(ioDispatcher) {
            try {
                val updatedCar = carsRepository.setAvailableForTrade(carToUpdate.id, !carToUpdate.availableForTrade)
                if (updatedCar != null) {
                    _carDetailState.update { currentState ->
                        if (currentState is CarDetailUiState.Success && currentState.car.id == updatedCar.id) {
                            CarDetailUiState.Success(updatedCar)
                        } else {
                            currentState // No change if not the detailed car or not in success state
                        }
                    }
                    _carsState.update { currentCarsState ->
                        if (currentCarsState is CarsUiState.Success) {
                            val updatedCars = currentCarsState.cars.map {
                                if (it.id == updatedCar.id) updatedCar else it
                            }
                            CarsUiState.Success(updatedCars)
                        } else {
                            currentCarsState
                        }
                    }
                } else {
                    logger.e(
                        "Failed to toggle trade availability, repository returned null for car ID: ${carToUpdate.id}",
                    )
                    // Revert optimistic update if necessary
                    if (originalDetailState is CarDetailUiState.Success &&
                        originalDetailState.car.id == carToUpdate.id
                    ) {
                        _carDetailState.update { originalDetailState }
                    }
                    // Optionally set a general error state for the detail view
                    _carDetailState.update { CarDetailUiState.Error }
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Error toggling trade availability for car ID: ${carToUpdate.id}", e)
                // Revert optimistic update
                if (originalDetailState is CarDetailUiState.Success && originalDetailState.car.id == carToUpdate.id) {
                    _carDetailState.update { originalDetailState }
                }
                // Optionally set a general error state for the detail view or a message to the user
                _carDetailState.update { CarDetailUiState.Error }
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
