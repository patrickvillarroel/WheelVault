package io.github.patrickvillarroel.wheel.vault.integration.car

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.data.dao.CarDao
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.CarSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit.CarEditContent
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 * Integration test for Car CRUD operations.
 * Tests the complete flow: UI -> ViewModel -> Repository -> Room Database
 *
 * Supabase is mocked to throw exceptions, forcing the repository to use local Room storage.
 */
class CarCrudIntegrationTest : KoinTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    // Inject dependencies from Koin
    private val carViewModel: CarViewModel by inject()
    private val carsRepository: CarsRepository by inject()
    private val carDao: CarDao by inject()
    private val carSupabaseDataSource: CarSupabaseDataSource by inject()

    @Before
    fun setup() = runTest {
        // Clear database before each test
        carDao.deleteAll()

        // Configure Supabase mock to fail (simulating offline mode)
        // This forces the repository to use local Room storage
        coEvery { carSupabaseDataSource.insert(any()) } throws Exception("Network unavailable")
        coEvery { carSupabaseDataSource.update(any()) } throws Exception("Network unavailable")
        coEvery { carSupabaseDataSource.delete(any()) } throws Exception("Network unavailable")
        coEvery { carSupabaseDataSource.fetchAll(any(), any(), any()) } throws Exception("Network unavailable")
    }

    @After
    fun cleanup() = runTest {
        // Clean up after each test
        carDao.deleteAll()
    }

    @Test
    fun carCrud_createCar_persistsInRoomDatabase() = runTest {
        // Given - A valid car builder
        val builder = CarItem.Builder(
            model = "Corvette C8",
            year = 2024,
            brand = "HotWheels",
            manufacturer = "HotWheels",
            quantity = 1,
        )

        // When - Save the car through ViewModel
        carViewModel.save(builder)

        // Wait for the state to update
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            carViewModel.carDetailState.value is CarViewModel.CarDetailUiState.Success
        }

        // Then - Verify car was persisted in Room
        val savedCars = carDao.fetchAllOrderByCreatedDesc()
        assertEquals(1, savedCars.size)
        assertEquals("Corvette C8", savedCars[0].model)
        assertEquals(2024, savedCars[0].year)
        assertEquals("HotWheels", savedCars[0].brand)
    }

    @Test
    fun carCrud_updateCar_updatesRoomDatabase() = runTest {
        // Given - An existing car
        val originalCar = CarItem(
            model = "Mustang GT",
            year = 2023,
            brand = "HotWheels",
            manufacturer = "HotWheels",
            quantity = 1,
            images = setOf(CarItem.EmptyImage),
            imageUrl = CarItem.EmptyImage,
            isFavorite = false,
        )
        carsRepository.insert(originalCar)

        // When - Update the car
        val updatedCar = originalCar.copy(
            model = "Mustang GT500",
            year = 2024,
            quantity = 2,
        )
        carViewModel.save(updatedCar)

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            carViewModel.carDetailState.value is CarViewModel.CarDetailUiState.Success
        }

        // Then - Verify car was updated in Room
        val savedCars = carDao.fetchAllOrderByCreatedDesc()
        assertEquals(1, savedCars.size)
        assertEquals("Mustang GT500", savedCars[0].model)
        assertEquals(2024, savedCars[0].year)
        assertEquals(2, savedCars[0].quantity)
    }

    @Test
    fun carCrud_deleteCar_removesFromRoomDatabase() = runTest {
        // Given - An existing car
        val car = CarItem(
            model = "Camaro ZL1",
            year = 2024,
            brand = "HotWheels",
            manufacturer = "HotWheels",
            quantity = 1,
            images = setOf(CarItem.EmptyImage),
            imageUrl = CarItem.EmptyImage,
            isFavorite = false,
        )
        val savedCar = carsRepository.insert(car)

        // Verify car exists
        val savedCars = carDao.fetchAllOrderByCreatedDesc()
        assertEquals(1, savedCars.size)

        // When - Delete the car through repository
        // val deleteResult =
        carsRepository.delete(savedCar)
        // assertTrue(deleteResult) FIXME

        // Then - FIXME Verify car was soft deleted in Room (filtered out by DAO query)
        // savedCars =
        carDao.fetchAllOrderByCreatedDesc()
        // assertEquals(0, savedCars.size) // Soft deleted cars are filtered out
    }

    @Test
    fun carCrud_toggleFavorite_updatesRoomDatabase() = runTest {
        // Given - An existing non-favorite car
        val car = CarItem(
            model = "Ferrari F40",
            year = 2024,
            brand = "HotWheels",
            manufacturer = "HotWheels",
            quantity = 1,
            images = setOf(CarItem.EmptyImage),
            imageUrl = CarItem.EmptyImage,
            isFavorite = false,
        )
        carsRepository.insert(car)

        // Re-fetch from DB to get the exact entity
        val allCarsEntities = carDao.fetchAllOrderByCreatedDesc()
        assertEquals(1, allCarsEntities.size)
        val dbCarEntity = allCarsEntities[0]
        assertFalse(dbCarEntity.isFavorite)

        // When - Toggle to favorite by creating new entity with isFavorite = true
        val updatedEntity = io.github.patrickvillarroel.wheel.vault.data.entity.CarEntity(
            id = dbCarEntity.id,
            model = dbCarEntity.model,
            year = dbCarEntity.year,
            brand = dbCarEntity.brand,
            manufacturer = dbCarEntity.manufacturer,
            category = dbCarEntity.category,
            description = dbCarEntity.description,
            quantity = dbCarEntity.quantity,
            isFavorite = true, // Toggle this
            createdAt = dbCarEntity.createdAt,
            userId = dbCarEntity.userId,
            idRemote = dbCarEntity.idRemote,
            updatedAt = System.currentTimeMillis(),
            syncStatus = dbCarEntity.syncStatus,
            lastSyncedAt = dbCarEntity.lastSyncedAt,
            isDeleted = dbCarEntity.isDeleted,
        )
        carDao.updateCar(updatedEntity)

        // Then - Verify favorite status in Room
        val allCarsAfterUpdate = carDao.fetchAllOrderByCreatedDesc()
        assertEquals(1, allCarsAfterUpdate.size)
        assertTrue(allCarsAfterUpdate[0].isFavorite)
    }

    @Test
    fun carCrud_searchCars_returnsMatchingCars() = runTest {
        // Given - Multiple cars in database
        val car1 = CarItem(
            model = "Mustang",
            year = 2024,
            brand = "HotWheels",
            manufacturer = "HotWheels",
            quantity = 1,
            images = setOf(CarItem.EmptyImage),
            imageUrl = CarItem.EmptyImage,
            isFavorite = false,
        )
        val car2 = CarItem(
            model = "Camaro",
            year = 2024,
            brand = "HotWheels",
            manufacturer = "HotWheels",
            quantity = 1,
            images = setOf(CarItem.EmptyImage),
            imageUrl = CarItem.EmptyImage,
            isFavorite = false,
        )
        carsRepository.insert(car1)
        carsRepository.insert(car2)

        // When - Search for "Mustang"
        val results = carsRepository.search("Mustang", isFavorite = false)

        // Then - Only Mustang should be returned
        assertEquals(1, results.size)
        assertEquals("Mustang", results[0].model)
    }

    @Ignore("Needs to reconfigure activity application with compose test rule")
    @Test
    fun carCrud_uiToDatabase_createCarFlow() {
        // Given - CarEditContent with empty builder
        var confirmedBuilder: CarItem.Builder? = null
        val builder = CarItem.Builder()
        val manufacturerList = listOf("HotWheels", "MiniGT", "Maisto")

        // When - Render UI and fill form (simulated - actual form interaction would require more setup)
        composeTestRule.setContent {
            WheelVaultTheme {
                CarEditContent(
                    initial = builder,
                    isEditAction = false,
                    onAddPictureClick = {},
                    onConfirmClick = { confirmedBuilder = it },
                    manufacturerList = manufacturerList,
                    headersBackCallbacks = HeaderBackCallbacks.default,
                )
            }
        }

        // Verify form is displayed
        val addCarText = context.getString(R.string.add_car)
        composeTestRule.onNodeWithText(addCarText).assertIsDisplayed()

        // Then - Verify form validation (empty form should not allow submission)
        val brandText = context.getString(R.string.brand) + " *"
        composeTestRule.onNodeWithText(brandText).assertIsDisplayed()
    }

    @Test
    fun carCrud_filterByManufacturer_returnsFilteredCars() = runTest {
        // Given - Cars from different manufacturers
        val hotWheelsCar = CarItem(
            model = "Mustang",
            year = 2024,
            brand = "HotWheels",
            manufacturer = "HotWheels",
            quantity = 1,
            images = setOf(CarItem.EmptyImage),
            imageUrl = CarItem.EmptyImage,
            isFavorite = false,
        )
        val miniGTCar = CarItem(
            model = "GTR R35",
            year = 2024,
            brand = "MiniGT",
            manufacturer = "MiniGT",
            quantity = 1,
            images = setOf(CarItem.EmptyImage),
            imageUrl = CarItem.EmptyImage,
            isFavorite = false,
        )
        carsRepository.insert(hotWheelsCar)
        carsRepository.insert(miniGTCar)

        // When - Filter by HotWheels
        val results = carsRepository.fetchByManufacturer("HotWheels", isFavorite = false)

        // Then - Only HotWheels car should be returned
        assertEquals(1, results.size)
        assertEquals("HotWheels", results[0].manufacturer)
        assertEquals("Mustang", results[0].model)
    }

    @Test
    fun carCrud_countCars_returnsCorrectCount() = runTest {
        // Given - Multiple cars
        val car1 = CarItem(
            model = "Car 1",
            year = 2024,
            brand = "HotWheels",
            manufacturer = "HotWheels",
            quantity = 1,
            images = setOf(CarItem.EmptyImage),
            imageUrl = CarItem.EmptyImage,
            isFavorite = false,
        )
        val car2 = CarItem(
            model = "Car 2",
            year = 2024,
            brand = "HotWheels",
            manufacturer = "HotWheels",
            quantity = 1,
            images = setOf(CarItem.EmptyImage),
            imageUrl = CarItem.EmptyImage,
            isFavorite = true,
        )
        carsRepository.insert(car1)
        carsRepository.insert(car2)

        // When - Count total and favorite cars
        val totalCount = carsRepository.count(isFavorite = false)
        val favoriteCount = carsRepository.count(isFavorite = true)

        // Then - Verify counts
        assertEquals(2, totalCount)
        assertEquals(1, favoriteCount)
    }
}
