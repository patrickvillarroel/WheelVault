package io.github.patrickvillarroel.wheel.vault.ui.screen

import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CarsViewModelTest {
    private lateinit var carItem: CarItem
    private lateinit var repository: CarsRepository
    private lateinit var viewModel: CarViewModel

    private val dispatcher = UnconfinedTestDispatcher()

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

        // Use mocks
        repository = mockk(relaxed = true)
        viewModel = CarViewModel(repository, dispatcher)
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

        // Assert
        coVerify(exactly = 1) { repository.update(carItem) }
        coVerify(exactly = 0) { repository.insert(any()) }
    }

    @Test
    fun saveShouldCallInsertWhenCarNotExist() = runTest {
        // Arrange
        coEvery { repository.exist(carItem.id) } returns false
        coEvery { repository.insert(any()) } returns carItem

        // Act
        viewModel.save(carItem)

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
}
