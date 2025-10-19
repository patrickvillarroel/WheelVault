package io.github.patrickvillarroel.wheel.vault.ui.screen

import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CarsViewModelTest {
    private lateinit var carItem: CarItem
    private lateinit var repository: CarsRepository
    private lateinit var viewModel: CarViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
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
    fun saveShouldCallUpdateWhenCarAlreadyExists() = runTest {
        // Arrange
        val builder = mockk<CarItem.Builder>()
        coEvery { builder.build() } returns carItem
        coEvery { repository.exist(carItem.id) } returns true
        coEvery { repository.update(any()) } returns carItem

        // Act
        viewModel.save(builder)

        // Assert
        coVerify(exactly = 1) { repository.update(carItem) }
        coVerify(exactly = 0) { repository.insert(any()) }
    }
}
