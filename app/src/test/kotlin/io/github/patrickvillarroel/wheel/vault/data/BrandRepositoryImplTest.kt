package io.github.patrickvillarroel.wheel.vault.data

import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageDownloadHelper
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageRepository
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.BrandRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.BrandSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.uuid.Uuid

class BrandRepositoryImplTest {

    private lateinit var supabaseDataSource: BrandSupabaseDataSource
    private lateinit var roomDataSource: BrandRoomDataSource
    private lateinit var imageHelper: ImageDownloadHelper
    private lateinit var imageRepository: ImageRepository
    private lateinit var repository: BrandRepositoryImpl

    @Before
    fun setup() {
        supabaseDataSource = mockk(relaxed = true)
        roomDataSource = mockk(relaxed = true)
        imageHelper = mockk(relaxed = true)
        imageRepository = mockk(relaxed = true)
        repository = BrandRepositoryImpl(
            supabase = supabaseDataSource,
            room = roomDataSource,
            imageHelper = imageHelper,
            imageRepository = imageRepository,
        )
    }

    @Test
    fun `search should return brands from supabase data source`() = runTest {
        // Given
        val query = "HotWheels"
        val expectedBrands = listOf(
            Brand(
                id = Uuid.random(),
                name = "HotWheels",
                description = "Premium die-cast cars",
                image = Brand.DEFAULT_IMAGE,
                contentDescription = null,
                createdAt = null,
            ),
        )
        coEvery { supabaseDataSource.search(query) } returns expectedBrands

        // When
        val result = repository.search(query)

        // Then
        result shouldHaveSize 1
        result shouldBe expectedBrands
        coVerify(exactly = 1) { supabaseDataSource.search(query) }
    }

    @Test
    fun `fetchAllNames should return brand names from room when not forcing refresh`() = runTest {
        // Given
        val expectedNames = listOf("HotWheels", "MiniGT", "Maisto")
        coEvery { roomDataSource.fetchAllNames() } returns expectedNames

        // When
        val result = repository.fetchAllNames(forceRefresh = false)

        // Then
        result shouldHaveSize 3
        result shouldBe expectedNames
        coVerify { roomDataSource.fetchAllNames() }
    }

    @Test
    fun `fetch should return brand from room when found locally and not forcing refresh`() = runTest {
        // Given
        val brandId = Uuid.random()
        val expectedBrand = Brand(
            id = brandId,
            name = "HotWheels",
            description = "Premium cars",
            image = Brand.DEFAULT_IMAGE,
            contentDescription = null,
            createdAt = null,
        )
        coEvery { roomDataSource.fetch(brandId, false) } returns expectedBrand

        // When
        val result = repository.fetch(brandId, forceRefresh = false)

        // Then
        result shouldNotBe null
        result shouldBe expectedBrand
        coVerify { roomDataSource.fetch(brandId, false) }
    }

    @Test
    fun `fetch should return null when brand not found`() = runTest {
        // Given
        val brandId = Uuid.random()
        coEvery { roomDataSource.fetch(brandId, false) } returns null
        coEvery { supabaseDataSource.fetch(brandId, false) } returns null

        // When
        val result = repository.fetch(brandId, forceRefresh = false)

        // Then
        result shouldBe null
    }

    @Test
    fun `fetchByName should return brand when found`() = runTest {
        // Given
        val brandName = "MiniGT"
        val expectedBrand = Brand(
            id = Uuid.random(),
            name = brandName,
            description = "High quality models",
            image = Brand.DEFAULT_IMAGE,
            contentDescription = null,
            createdAt = null,
        )
        coEvery { roomDataSource.fetchByName(brandName) } returns expectedBrand

        // When
        val result = repository.fetchByName(brandName)

        // Then
        result shouldNotBe null
        result shouldBe expectedBrand
        coVerify { roomDataSource.fetchByName(brandName) }
    }

    @Test
    fun `fetchByName should return null when brand not found by name`() = runTest {
        // Given
        val brandName = "NonExistentBrand"
        coEvery { roomDataSource.fetchByName(brandName) } returns null
        coEvery { supabaseDataSource.fetchByName(brandName) } returns null

        // When
        val result = repository.fetchByName(brandName)

        // Then
        result shouldBe null
    }

    @Test
    fun `fetchByDescription should return brand when found by description`() = runTest {
        // Given
        val description = "Premium die-cast"
        val expectedBrand = Brand(
            id = Uuid.random(),
            name = "HotWheels",
            description = description,
            image = Brand.DEFAULT_IMAGE,
            contentDescription = null,
            createdAt = null,
        )
        coEvery { roomDataSource.fetchByDescription(description) } returns expectedBrand

        // When
        val result = repository.fetchByDescription(description)

        // Then
        result shouldNotBe null
        result shouldBe expectedBrand
        coVerify { roomDataSource.fetchByDescription(description) }
    }

    @Test
    fun `fetchAll should return list of brands from room when not forcing refresh`() = runTest {
        // Given
        val expectedBrands = listOf(
            Brand(
                id = Uuid.random(),
                name = "HotWheels",
                description = "Premium cars",
                image = Brand.DEFAULT_IMAGE,
                contentDescription = null,
                createdAt = null,
            ),
            Brand(
                id = Uuid.random(),
                name = "MiniGT",
                description = "High quality",
                image = Brand.DEFAULT_IMAGE,
                contentDescription = null,
                createdAt = null,
            ),
        )
        coEvery { roomDataSource.fetchAll(false) } returns expectedBrands

        // When
        val result = repository.fetchAll(forceRefresh = false)

        // Then
        result shouldHaveSize 2
        result shouldBe expectedBrands
        coVerify { roomDataSource.fetchAll(false) }
    }

    @Test
    fun `fetchAllImages should return map of brand images`() = runTest {
        // Given
        val brandId1 = Uuid.random()
        val brandId2 = Uuid.random()
        val expectedImages = mapOf(
            brandId1 to "image1.png",
            brandId2 to "image2.png",
        )
        coEvery { roomDataSource.fetchAllImages(false) } returns expectedImages

        // When
        val result = repository.fetchAllImages(forceRefresh = false)

        // Then
        result shouldBe expectedImages
        coVerify { roomDataSource.fetchAllImages(false) }
    }
}
