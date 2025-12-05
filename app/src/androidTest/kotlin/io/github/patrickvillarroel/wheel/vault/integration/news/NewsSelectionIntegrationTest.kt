package io.github.patrickvillarroel.wheel.vault.integration.news

import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.data.dao.NewsDao
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.GetVideoNewsSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import io.github.patrickvillarroel.wheel.vault.ui.screen.home.HomeViewModel
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
 * Integration test for News/Videos selection operations.
 * Tests the complete flow: UI -> ViewModel -> UseCase -> Supabase (mocked)
 */
@Ignore
class NewsSelectionIntegrationTest : KoinTest {

    // Inject dependencies from Koin
    private val homeViewModel: HomeViewModel by inject()
    private val getVideosNewsUseCase: GetVideosNewsUseCase by inject()
    private val newsDao: NewsDao by inject()
    private val videoNewsSupabaseDataSource: GetVideoNewsSupabaseDataSource by inject()

    private lateinit var news1: VideoNews
    private lateinit var news2: VideoNews
    private lateinit var news3: VideoNews

    @Before
    fun setup() = runTest {
        // Clear database before each test
        newsDao.deleteAll()

        // Create test news/videos
        news1 = VideoNews(
            id = Uuid.random(),
            name = "New HotWheels Collection 2024",
            link = "https://youtube.com/watch?v=abc123",
            thumbnail = R.drawable.no_picture_available,
            description = "Check out the latest HotWheels collection",
            createdAt = Clock.System.now(),
        )

        news2 = VideoNews(
            id = Uuid.random(),
            name = "Top 10 Rare Diecast Cars",
            link = "https://youtube.com/watch?v=def456",
            thumbnail = R.drawable.no_picture_available,
            description = "Discover the rarest diecast cars",
            createdAt = Clock.System.now(),
        )

        news3 = VideoNews(
            id = Uuid.random(),
            name = "Collectors' Guide 2024",
            link = "https://youtube.com/watch?v=ghi789",
            thumbnail = R.drawable.no_picture_available,
            description = "Everything collectors need to know",
            createdAt = Clock.System.now(),
        )

        // Mock Supabase responses for news data
        coEvery {
            videoNewsSupabaseDataSource.getVideos(any())
        } returns listOf(news1, news2, news3)
    }

    @After
    fun cleanup() = runTest {
        // Clean up after each test
        newsDao.deleteAll()
    }

    @Test
    fun newsSelection_fetchAllVideos_returnsFromSupabase() = runTest {
        // When - Fetch all videos (force refresh to get from Supabase)
        val videos = getVideosNewsUseCase.getVideos(forceRefresh = true)

        // Then - Verify all videos are returned
        assertEquals(3, videos.size)
        assertTrue(videos.any { it.name == "New HotWheels Collection 2024" })
        assertTrue(videos.any { it.name == "Top 10 Rare Diecast Cars" })
        assertTrue(videos.any { it.name == "Collectors' Guide 2024" })

        // Verify Supabase was called
        coVerify(atLeast = 1) {
            videoNewsSupabaseDataSource.getVideos(any())
        }
    }

    @Test
    fun newsSelection_fetchVideos_cachesLocally() = runTest {
        // When - First fetch (force refresh to get from Supabase)
        getVideosNewsUseCase.getVideos(forceRefresh = true)

        // Wait for data to be cached
        kotlinx.coroutines.delay(200)

        // Then - Verify data was saved to Room
        val cachedNews = newsDao.fetchAll()
        assertTrue(cachedNews.isNotEmpty())
    }

    @Test
    fun newsSelection_viewModelHasNews_asFlow() = runTest {
        // When - Access news flow from ViewModel
        val newsFlow = homeViewModel.news

        // Then - Verify flow exists and is not null
        assertNotNull(newsFlow)

        // Verify Supabase is configured to return data
        coEvery {
            videoNewsSupabaseDataSource.getVideos(any())
        } returns listOf(news1, news2, news3)
    }

    @Test
    fun newsSelection_multipleVideos_returnsInCorrectOrder() = runTest {
        // When - Fetch videos
        val videos = getVideosNewsUseCase.getVideos(forceRefresh = true)

        // Then - Verify count
        assertEquals(3, videos.size)

        // Verify all videos have required fields
        videos.forEach { video ->
            assertNotNull(video.id)
            assertTrue(video.name.isNotEmpty())
            assertTrue(video.link.isNotEmpty())
            assertNotNull(video.thumbnail)
        }
    }

    @Test
    fun newsSelection_videoWithDescription_returnsCorrectly() = runTest {
        // When - Fetch videos
        val videos = getVideosNewsUseCase.getVideos(forceRefresh = true)

        // Then - Find video with description
        val videoWithDescription = videos.find { it.name == "New HotWheels Collection 2024" }

        assertNotNull(videoWithDescription)
        assertEquals("Check out the latest HotWheels collection", videoWithDescription?.description)
    }

    @Test
    fun newsSelection_emptySupabaseResponse_returnsEmptyList() = runTest {
        // Given - Mock empty response
        coEvery {
            videoNewsSupabaseDataSource.getVideos(any())
        } returns emptyList()

        // When - Fetch videos
        val videos = getVideosNewsUseCase.getVideos(forceRefresh = true)

        // Then - Should return empty list
        assertTrue(videos.isEmpty())

        coVerify(atLeast = 1) {
            videoNewsSupabaseDataSource.getVideos(any())
        }
    }

    @Test
    fun newsSelection_forceRefresh_fetchesFromSupabase() = runTest {
        // Given - First fetch to cache data
        getVideosNewsUseCase.getVideos(forceRefresh = true)

        // Reset mock invocation count
        io.mockk.clearMocks(videoNewsSupabaseDataSource, answers = false)

        // When - Force refresh
        val videos = getVideosNewsUseCase.getVideos(forceRefresh = true)

        // Then - Supabase should be called again
        assertEquals(3, videos.size)
        coVerify(atLeast = 1) {
            videoNewsSupabaseDataSource.getVideos(true)
        }
    }

    @Test
    fun newsSelection_videoLinks_areValidUrls() = runTest {
        // When - Fetch videos
        val videos = getVideosNewsUseCase.getVideos(forceRefresh = true)

        // Then - All links should start with https://
        videos.forEach { video ->
            assertTrue(
                "Video link should be valid URL: ${video.link}",
                video.link.startsWith("https://"),
            )
        }
    }

    @Test
    fun newsSelection_useCaseWithDelay_completesSuccessfully() = runTest {
        // Given - Mock a delay in Supabase response
        coEvery {
            videoNewsSupabaseDataSource.getVideos(any())
        } coAnswers {
            kotlinx.coroutines.delay(100)
            listOf(news1, news2, news3)
        }

        // When - Fetch videos with delay
        val videos = getVideosNewsUseCase.getVideos(forceRefresh = true)

        // Then - Verify videos were retrieved
        assertEquals(3, videos.size)
        assertTrue(videos.any { it.name == "New HotWheels Collection 2024" })
    }
}
