package io.github.patrickvillarroel.wheel.vault.ui.screen
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class CarViewModelTest {

    private lateinit var carsRepository: CarsRepository
    private lateinit var viewModel: CarViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        carsRepository = mockk(relaxed = true)
        viewModel = CarViewModel(carsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `findById should update state to Loading and then Success when car is found`() = runTest {
        // Given
        val carId = Uuid.random()
        val expectedCar = CarItem(
            model = "Test Model",
            year = 2024,
            brand = "Test Brand",
            manufacturer = "Test Manufacturer",
            id = carId,
            images = setOf(CarItem.EmptyImage),
            isFavorite = false,
            availableForTrade = false,
        )
        coEvery { carsRepository.fetch(carId) } returns expectedCar

        // When
        viewModel.findById(carId)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.carDetailState.first()
        finalState.shouldBeInstanceOf<CarViewModel.CarDetailUiState.Success>()
        finalState.car shouldBe expectedCar
        coVerify(exactly = 1) { carsRepository.fetch(carId) }
    }

    @Test
    fun `findById should update state to NotFound when car is not found`() = runTest {
        // Given
        val carId = Uuid.random()
        coEvery { carsRepository.fetch(carId) } returns null

        // When
        viewModel.findById(carId)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.carDetailState.first()
        finalState.shouldBeInstanceOf<CarViewModel.CarDetailUiState.NotFound>()
        coVerify(exactly = 1) { carsRepository.fetch(carId) }
    }

    @Test
    fun `findById should update state to Error when repository throws exception`() = runTest {
        // Given
        val carId = Uuid.random()
        coEvery { carsRepository.fetch(carId) } throws RuntimeException("Database error")

        // When
        viewModel.findById(carId)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.carDetailState.first()
        finalState.shouldBeInstanceOf<CarViewModel.CarDetailUiState.Error>()
    }

    @Test
    fun `delete should remove car and update state to Idle when successful`() = runTest {
        // Given
        val car = CarItem(
            model = "Test Model",
            year = 2024,
            brand = "Test Brand",
            manufacturer = "Test Manufacturer",
            id = Uuid.random(),
            images = setOf(CarItem.EmptyImage),
            isFavorite = false,
            availableForTrade = false,
        )
        coEvery { carsRepository.delete(car) } returns true

        // When
        viewModel.delete(car)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { carsRepository.delete(car) }
    }

    @Test
    fun `delete should update state to Error when deletion fails`() = runTest {
        // Given
        val car = CarItem(
            model = "Test Model",
            year = 2024,
            brand = "Test Brand",
            manufacturer = "Test Manufacturer",
            id = Uuid.random(),
            images = setOf(CarItem.EmptyImage),
            isFavorite = false,
            availableForTrade = false,
        )
        coEvery { carsRepository.delete(car) } returns false

        // When
        viewModel.delete(car)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.carDetailState.first()
        finalState.shouldBeInstanceOf<CarViewModel.CarDetailUiState.Error>()
    }

    @Test
    fun `toggleCarTradeAvailability should update car's trade availability`() = runTest {
        // Given
        val car = CarItem(
            model = "Test Model",
            year = 2024,
            brand = "Test Brand",
            manufacturer = "Test Manufacturer",
            id = Uuid.random(),
            images = setOf(CarItem.EmptyImage),
            isFavorite = false,
            availableForTrade = false,
        )
        val updatedCar = car.copy(availableForTrade = true)
        coEvery { carsRepository.setAvailableForTrade(car.id, true) } returns updatedCar

        // When
        viewModel.toggleCarTradeAvailability(car)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { carsRepository.setAvailableForTrade(car.id, true) }
    }

    @Test
    fun `save should insert new car when it doesn't exist`() = runTest {
        // Given
        val car = CarItem(
            model = "New Car",
            year = 2024,
            brand = "New Brand",
            manufacturer = "New Manufacturer",
            id = Uuid.random(),
            images = setOf(CarItem.EmptyImage),
            isFavorite = false,
            availableForTrade = false,
        )
        coEvery { carsRepository.exist(car.id) } returns false
        coEvery { carsRepository.insert(any()) } returns car

        // When
        viewModel.save(car, null)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { carsRepository.exist(car.id) }
        coVerify(exactly = 1) { carsRepository.insert(any()) }
        val finalState = viewModel.carDetailState.first()
        finalState.shouldBeInstanceOf<CarViewModel.CarDetailUiState.Success>()
    }

    @Test
    fun `save should update existing car when it exists`() = runTest {
        // Given
        val car = CarItem(
            model = "Existing Car",
            year = 2024,
            brand = "Existing Brand",
            manufacturer = "Existing Manufacturer",
            id = Uuid.random(),
            images = setOf(CarItem.EmptyImage),
            isFavorite = false,
            availableForTrade = false,
        )
        coEvery { carsRepository.exist(car.id) } returns true
        coEvery { carsRepository.update(any()) } returns car

        // When
        viewModel.save(car, null)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { carsRepository.exist(car.id) }
        coVerify(exactly = 1) { carsRepository.update(any()) }
        val finalState = viewModel.carDetailState.first()
        finalState.shouldBeInstanceOf<CarViewModel.CarDetailUiState.Success>()
    }
}
