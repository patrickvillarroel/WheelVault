package io.github.patrickvillarroel.wheel.vault.ui.screen

import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
    private lateinit var carItem: CarItem
    private lateinit var repository: CarsRepository
    private lateinit var viewModel: CarViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        carItem = CarItem.Builder(
            model = "Golf",
            year = 2014,
            manufacturer = "Hot Wheels",
            quantity = 1,
            isFavorite = true,
            availableForTrade = false,
            description = null,
            category = null,
        ).build().shouldNotBeNull()

        Dispatchers.setMain(testDispatcher)

        // Use mocks
        repository = mockk(relaxed = true)
        viewModel = CarViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun saveShouldCallUpdateBuilderWhenCarAlreadyExists() = runTest {
        // Arrange
        val builder = mockk<CarItem.Builder>()
        every { builder.build() } returns carItem
        coEvery { repository.exist(carItem.id) } returns true
        coEvery { repository.update(any()) } returns carItem

        // Act
        viewModel.save(builder)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.update(carItem) }
        coVerify(exactly = 0) { repository.insert(any()) }
    }

    @Test
    fun saveShouldCallUpdateWhenCarAlreadyExists() = runTest {
        // Arrange
        coEvery { repository.exist(carItem.id) } returns true
        coEvery { repository.update(any()) } returns carItem

        // Act
        viewModel.save(carItem)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.update(any()) }
        coVerify(exactly = 0) { repository.insert(any()) }
    }

    @Test
    fun saveShouldCallInsertWhenCarNotExist() = runTest {
        // Arrange
        coEvery { repository.exist(carItem.id) } returns false
        coEvery { repository.insert(any()) } returns carItem

        // Act
        viewModel.save(carItem)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 0) { repository.update(any()) }
        coVerify(exactly = 1) { repository.insert(carItem) }
    }

    @Test
    fun saveShouldCallInsertWhenCarBuilderNotExist() = runTest {
        // Arrange
        val carBuilderMockk = mockk<CarItem.Builder>()
        every { carBuilderMockk.build() } returns carItem
        coEvery { repository.exist(carItem.id) } returns false
        coEvery { repository.insert(any()) } returns carItem

        // Act
        viewModel.save(carBuilderMockk)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 0) { repository.update(any()) }
        coVerify(exactly = 1) { repository.insert(carItem) }
    }

    @Test
    fun toggleCarTradeAvailabilityTrueToFalseWhenRepoReturnsCar() = runTest {
        // given
        val currentCar = carItem.copy(availableForTrade = true)
        val updatedCar = currentCar.copy(availableForTrade = false)
        val carId = currentCar.id

        // when
        coEvery { repository.setAvailableForTrade(carId, false) } returns updatedCar

        viewModel.toggleCarTradeAvailability(currentCar)
        advanceUntilIdle()

        // then
        coVerify(exactly = 1) { repository.setAvailableForTrade(carId, false) }
    }

    @Test
    fun toggleCarTradeAvailabilityFalseToTrueWhenRepoReturnsCar() = runTest {
        // given
        val currentCar = carItem.copy(availableForTrade = false)
        val updatedCar = currentCar.copy(availableForTrade = true)
        val carId = currentCar.id

        // when
        coEvery { repository.setAvailableForTrade(carId, true) } returns updatedCar

        viewModel.toggleCarTradeAvailability(currentCar)
        advanceUntilIdle()

        // then
        coVerify(exactly = 1) { repository.setAvailableForTrade(carId, true) }
    }

    @Test
    fun toggleCarTradeAvailabilityTrueToFalseWhenRepoReturnsNullShouldStateError() = runTest {
        // given
        val currentCar = carItem.copy(availableForTrade = true)
        val carId = currentCar.id

        // when
        coEvery { repository.setAvailableForTrade(carId, false) } returns null

        viewModel.toggleCarTradeAvailability(currentCar)
        advanceUntilIdle()

        // then
        coVerify(exactly = 1) { repository.setAvailableForTrade(carId, false) }
        viewModel.carDetailState.first() shouldBeEqual CarViewModel.CarDetailUiState.Error
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
        coEvery { repository.fetch(carId) } returns expectedCar

        // When
        viewModel.findById(carId)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.carDetailState.first()
        finalState.shouldBeInstanceOf<CarViewModel.CarDetailUiState.Success>()
        finalState.car shouldBe expectedCar
        coVerify(exactly = 1) { repository.fetch(carId) }
    }

    @Test
    fun `findById should update state to NotFound when car is not found`() = runTest {
        // Given
        val carId = Uuid.random()
        coEvery { repository.fetch(carId) } returns null

        // When
        viewModel.findById(carId)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.carDetailState.first()
        finalState.shouldBeInstanceOf<CarViewModel.CarDetailUiState.NotFound>()
        coVerify(exactly = 1) { repository.fetch(carId) }
    }

    @Test
    fun `findById should update state to Error when repository throws exception`() = runTest {
        // Given
        val carId = Uuid.random()
        coEvery { repository.fetch(carId) } throws RuntimeException("Database error")

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
        coEvery { repository.delete(car) } returns true

        // When
        viewModel.delete(car)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { repository.delete(car) }
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
        coEvery { repository.delete(car) } returns false

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
        coEvery { repository.setAvailableForTrade(car.id, true) } returns updatedCar

        // When
        viewModel.toggleCarTradeAvailability(car)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { repository.setAvailableForTrade(car.id, true) }
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
        coEvery { repository.exist(car.id) } returns false
        coEvery { repository.insert(any()) } returns car

        // When
        viewModel.save(car, null)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { repository.exist(car.id) }
        coVerify(exactly = 1) { repository.insert(any()) }
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
        coEvery { repository.exist(car.id) } returns true
        coEvery { repository.update(any()) } returns car

        // When
        viewModel.save(car, null)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { repository.exist(car.id) }
        coVerify(exactly = 1) { repository.update(any()) }
        val finalState = viewModel.carDetailState.first()
        finalState.shouldBeInstanceOf<CarViewModel.CarDetailUiState.Success>()
    }
}
