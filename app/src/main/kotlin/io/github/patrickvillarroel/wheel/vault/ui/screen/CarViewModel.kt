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
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid
import android.content.Context as AndroidContext
import coil3.Uri as Coil3Uri

class CarViewModel(private val carsRepository: CarsRepository) : ViewModel() {
    companion object {
        private val logger = Logger.withTag("Car VM")
    }
    private val _carDetailState = MutableStateFlow<CarDetailUiState>(CarDetailUiState.Idle)
    val carDetailState = _carDetailState.asStateFlow()

    fun findById(id: Uuid, force: Boolean = false) {
        _carDetailState.update { CarDetailUiState.Loading }
        viewModelScope.launch {
            try {
                logger.d { "Going to find car by id $id" }
                // TODO pass force param to fetch
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
        logger.v { "Going to map car builder to save $car" }
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
        logger.v("Going to save this car $car")
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
            viewModelScope.launch {
                try {
                    val newCarState = if (carsRepository.exist(carToSave.id)) {
                        logger.i("The car exist")
                        carsRepository.update(carToSave)
                    } else {
                        carsRepository.insert(carToSave)
                    }
                    logger.d("New car state: $newCarState")
                    _carDetailState.update { CarDetailUiState.Success(newCarState) }
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
        viewModelScope.launch {
            try {
                if (!carsRepository.delete(car)) {
                    logger.e("Failed to delete car")
                    _carDetailState.update { CarDetailUiState.Error }
                    return@launch
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

        viewModelScope.launch {
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

    sealed interface CarDetailUiState {
        data object Idle : CarDetailUiState
        data object Loading : CarDetailUiState
        data object NotFound : CarDetailUiState

        @Immutable
        data class Success(@Stable val car: CarItem) : CarDetailUiState
        data object Error : CarDetailUiState
    }
}
