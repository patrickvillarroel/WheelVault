package io.github.patrickvillarroel.wheel.vault.ui.screen

import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.kotest.matchers.collections.shouldHaveSize
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
class BrandViewModelTest {

    private lateinit var brandRepository: BrandRepository
    private lateinit var carRepository: CarsRepository
    private lateinit var viewModel: BrandViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        brandRepository = mockk(relaxed = true)
        carRepository = mockk(relaxed = true)
        viewModel = BrandViewModel(brandRepository, carRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchNames should update brandsNames state with brand names`() = runTest {
        // Given
        val expectedNames = listOf("HotWheels", "MiniGT", "Maisto")
        coEvery { brandRepository.fetchAllNames(false) } returns expectedNames

        // When
        viewModel.fetchNames(force = false)
        advanceUntilIdle()

        // Then
        val names = viewModel.brandsNames.first()
        names shouldHaveSize 3
        names shouldBe expectedNames
        coVerify(exactly = 1) { brandRepository.fetchAllNames(false) }
    }

    @Test
    fun `fetchNames with force should call repository with force parameter`() = runTest {
        // Given
        val expectedNames = listOf("HotWheels", "MiniGT")
        coEvery { brandRepository.fetchAllNames(true) } returns expectedNames

        // When
        viewModel.fetchNames(force = true)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { brandRepository.fetchAllNames(true) }
    }

    @Test
    fun `fetchNames should handle exceptions gracefully`() = runTest {
        // Given
        coEvery { brandRepository.fetchAllNames(any()) } throws RuntimeException("Network error")

        // When
        viewModel.fetchNames()
        advanceUntilIdle()

        // Then - Should not crash, state should remain as previous
        val names = viewModel.brandsNames.first()
        names shouldBe emptyList()
    }

    @Test
    fun `findById should update state to Success when brand is found`() = runTest {
        // Given
        val brandId = Uuid.random()
        val expectedBrand = Brand(
            id = brandId,
            name = "HotWheels",
            description = "Test description",
            image = Brand.DEFAULT_IMAGE,
            contentDescription = null,
            createdAt = null,
        )
        val expectedCars = listOf(
            CarItem(
                model = "Test Car 1",
                year = 2024,
                brand = "HotWheels",
                manufacturer = "HotWheels",
                id = Uuid.random(),
                images = setOf(CarItem.EmptyImage),
                isFavorite = false,
                availableForTrade = false,
            ),
            CarItem(
                model = "Test Car 2",
                year = 2023,
                brand = "HotWheels",
                manufacturer = "HotWheels",
                id = Uuid.random(),
                images = setOf(CarItem.EmptyImage),
                isFavorite = false,
                availableForTrade = false,
            ),
        )
        coEvery { brandRepository.fetch(brandId) } returns expectedBrand
        coEvery { carRepository.fetchByManufacturer("HotWheels") } returns expectedCars

        // When
        viewModel.findById(brandId)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.brandDetailsState.first()
        finalState.shouldBeInstanceOf<BrandViewModel.BrandDetailsUiState.Success>()
        finalState.brand shouldBe expectedBrand
        finalState.cars shouldHaveSize 2
        finalState.cars shouldBe expectedCars
        coVerify(exactly = 1) { brandRepository.fetch(brandId) }
        coVerify(exactly = 1) { carRepository.fetchByManufacturer("HotWheels") }
    }

    @Test
    fun `findById should update state to NotFound when brand is not found`() = runTest {
        // Given
        val brandId = Uuid.random()
        coEvery { brandRepository.fetch(brandId) } returns null

        // When
        viewModel.findById(brandId)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.brandDetailsState.first()
        finalState.shouldBeInstanceOf<BrandViewModel.BrandDetailsUiState.NotFound>()
        coVerify(exactly = 1) { brandRepository.fetch(brandId) }
        coVerify(exactly = 0) { carRepository.fetchByManufacturer(any()) }
    }

    @Test
    fun `findById should update state to Error when repository throws exception`() = runTest {
        // Given
        val brandId = Uuid.random()
        coEvery { brandRepository.fetch(brandId) } throws RuntimeException("Database error")

        // When
        viewModel.findById(brandId)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.brandDetailsState.first()
        finalState.shouldBeInstanceOf<BrandViewModel.BrandDetailsUiState.Error>()
    }

    @Test
    fun `findById should update state to Loading before fetching data`() = runTest {
        // Given
        val brandId = Uuid.random()
        val expectedBrand = Brand(
            id = brandId,
            name = "MiniGT",
            description = "Test",
            image = Brand.DEFAULT_IMAGE,
            contentDescription = null,
            createdAt = null,
        )
        coEvery { brandRepository.fetch(brandId) } returns expectedBrand
        coEvery { carRepository.fetchByManufacturer("MiniGT") } returns emptyList()

        // When
        viewModel.findById(brandId)

        // Then - State should be Loading immediately
        val loadingState = viewModel.brandDetailsState.value
        loadingState.shouldBeInstanceOf<BrandViewModel.BrandDetailsUiState.Loading>()

        advanceUntilIdle()
        val finalState = viewModel.brandDetailsState.first()
        finalState.shouldBeInstanceOf<BrandViewModel.BrandDetailsUiState.Success>()
    }

    @Test
    fun `initial state should be Idle for brand details`() = runTest {
        // Then
        val initialState = viewModel.brandDetailsState.first()
        initialState.shouldBeInstanceOf<BrandViewModel.BrandDetailsUiState.Idle>()
    }

    @Test
    fun `initial state should have empty brand names list`() = runTest {
        // Then
        val initialNames = viewModel.brandsNames.first()
        initialNames shouldBe emptyList()
    }
}
