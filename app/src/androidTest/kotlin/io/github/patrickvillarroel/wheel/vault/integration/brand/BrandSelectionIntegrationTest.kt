package io.github.patrickvillarroel.wheel.vault.integration.brand

import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.data.dao.BrandDao
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.BrandSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.time.Clock
import kotlin.uuid.Uuid

/**
 * Integration test for Brand selection operations.
 * Tests the complete flow: UI -> ViewModel -> Repository -> Room/Supabase
 */
@Ignore
class BrandSelectionIntegrationTest : KoinTest {

    // Inject dependencies from Koin
    private val brandViewModel: BrandViewModel by inject()
    private val brandRepository: BrandRepository by inject()
    private val brandDao: BrandDao by inject()
    private val brandSupabaseDataSource: BrandSupabaseDataSource by inject()

    private lateinit var brand1: Brand
    private lateinit var brand2: Brand
    private lateinit var brand3: Brand

    @Before
    fun setup() = runTest {
        // Clear database before each test
        brandDao.deleteAll()

        // Create test brands
        brand1 = Brand(
            id = Uuid.random(),
            name = "HotWheels",
            description = "HotWheels brand description",
            image = R.drawable.no_picture_available,
            contentDescription = "HotWheels logo",
            createdAt = Clock.System.now(),
        )

        brand2 = Brand(
            id = Uuid.random(),
            name = "MiniGT",
            description = "MiniGT brand description",
            image = R.drawable.no_picture_available,
            contentDescription = "MiniGT logo",
            createdAt = Clock.System.now(),
        )

        brand3 = Brand(
            id = Uuid.random(),
            name = "Maisto",
            description = "Maisto brand description",
            image = R.drawable.no_picture_available,
            contentDescription = "Maisto logo",
            createdAt = Clock.System.now(),
        )

        // Mock Supabase responses for brand data
        coEvery {
            brandSupabaseDataSource.fetchAll()
        } returns listOf(brand1, brand2, brand3)

        coEvery {
            brandSupabaseDataSource.fetchAllNames()
        } returns listOf("HotWheels", "MiniGT", "Maisto")

        coEvery {
            brandSupabaseDataSource.search(any())
        } answers {
            val query = firstArg<String>()
            listOf(brand1, brand2, brand3).filter { it.name.contains(query, ignoreCase = true) }
        }

        coEvery {
            brandSupabaseDataSource.fetch(brand1.id)
        } returns brand1

        coEvery {
            brandSupabaseDataSource.fetchByName("HotWheels")
        } returns brand1
    }

    @After
    fun cleanup() = runTest {
        // Clean up after each test
        brandDao.deleteAll()
    }

    @Test
    fun brandSelection_fetchAllBrands_returnsFromSupabase() = runTest {
        // When - Fetch all brands (force refresh to get from Supabase)
        val brands = brandRepository.fetchAll(forceRefresh = true)

        // Then - Verify all brands are returned
        assertEquals(3, brands.size)
        assertTrue(brands.any { it.name == "HotWheels" })
        assertTrue(brands.any { it.name == "MiniGT" })
        assertTrue(brands.any { it.name == "Maisto" })

        // Verify Supabase was called
        coVerify(exactly = 1) {
            brandSupabaseDataSource.fetchAll()
        }
    }

    @Test
    fun brandSelection_fetchBrandNames_returnsFromSupabase() = runTest {
        // When - Fetch brand names through ViewModel
        brandViewModel.fetchNames(force = true)

        // Wait for state to update
        kotlinx.coroutines.delay(500)

        // Then - Verify names were fetched
        val names = brandViewModel.brandsNames.value
        assertEquals(3, names.size)
        assertTrue(names.contains("HotWheels"))
        assertTrue(names.contains("MiniGT"))
        assertTrue(names.contains("Maisto"))

        // Verify Supabase was called
        coVerify(atLeast = 1) {
            brandSupabaseDataSource.fetchAllNames()
        }
    }

    @Test
    fun brandSelection_searchBrand_returnsMatchingResults() = runTest {
        // When - Search for "Hot"
        val results = brandRepository.search("Hot")

        // Then - Only HotWheels should be returned
        assertEquals(1, results.size)
        assertEquals("HotWheels", results[0].name)

        coVerify(exactly = 1) {
            brandSupabaseDataSource.search("Hot")
        }
    }

    @Test
    fun brandSelection_fetchBrandById_returnsCorrectBrand() = runTest {
        // When - Fetch brand by ID
        val brand = brandRepository.fetch(brand1.id, forceRefresh = true)

        // Then - Verify correct brand is returned
        assertNotNull(brand)
        assertEquals("HotWheels", brand?.name)
        assertEquals(brand1.id, brand?.id)

        coVerify(exactly = 1) {
            brandSupabaseDataSource.fetch(brand1.id)
        }
    }

    @Test
    fun brandSelection_fetchBrandByName_returnsCorrectBrand() = runTest {
        // When - Fetch brand by name
        val brand = brandRepository.fetchByName("HotWheels")

        // Then - Verify correct brand is returned
        assertNotNull(brand)
        assertEquals("HotWheels", brand?.name)
        assertEquals(brand1.id, brand?.id)

        coVerify(exactly = 1) {
            brandSupabaseDataSource.fetchByName("HotWheels")
        }
    }

    @Test
    fun brandSelection_viewModelFetchBrandDetails_updatesState() = runTest {
        // Given - Mock the fetch by ID
        coEvery {
            brandSupabaseDataSource.fetch(brand1.id)
        } returns brand1

        // When - Fetch brand details through repository
        val brand = brandRepository.fetch(brand1.id, forceRefresh = true)

        // Then - Verify brand was fetched
        assertNotNull(brand)
        assertEquals("HotWheels", brand?.name)
        assertEquals(brand1.id, brand?.id)

        coVerify(atLeast = 1) {
            brandSupabaseDataSource.fetch(brand1.id)
        }
    }

    @Test
    fun brandSelection_fetchAllImages_returnsImageMap() = runTest {
        // Given - Mock image data
        val imageMap = mapOf(
            brand1.id to brand1.image,
            brand2.id to brand2.image,
            brand3.id to brand3.image,
        )

        coEvery {
            brandSupabaseDataSource.fetchAllImages()
        } returns imageMap

        // When - Fetch all images
        val images = brandRepository.fetchAllImages(forceRefresh = true)

        // Then - Verify all images are returned
        assertEquals(3, images.size)
        assertTrue(images.containsKey(brand1.id))
        assertTrue(images.containsKey(brand2.id))
        assertTrue(images.containsKey(brand3.id))

        coVerify(exactly = 1) {
            brandSupabaseDataSource.fetchAllImages()
        }
    }

    @Test
    fun brandSelection_cachesBrandsLocally_afterFirstFetch() = runTest {
        // When - First fetch (force refresh to get from Supabase)
        brandRepository.fetchAll(forceRefresh = true)

        // Wait for data to be cached
        kotlinx.coroutines.delay(100)

        // Then - Verify data was saved to Room
        val cachedBrands = brandDao.fetchAll()
        assertTrue(cachedBrands.isNotEmpty())
        assertTrue(cachedBrands.any { it.name == "HotWheels" })
    }

    @Test
    fun brandSelection_searchEmptyQuery_returnsAllBrands() = runTest {
        // Given - Mock search with empty query
        coEvery {
            brandSupabaseDataSource.search("")
        } returns listOf(brand1, brand2, brand3)

        // When - Search with empty query
        val results = brandRepository.search("")

        // Then - All brands should be returned
        assertEquals(3, results.size)

        coVerify(exactly = 1) {
            brandSupabaseDataSource.search("")
        }
    }
}
