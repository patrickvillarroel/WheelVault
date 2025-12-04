package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.core.app.ApplicationProvider
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HomeCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.uuid.Uuid

class HomeContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun homeContent_displaysAllSections() {
        // Given
        val brandsPagingData = PagingData.from<Pair<Uuid, Any>>(
            data = listOf(Uuid.random() to R.drawable.hot_wheels_logo_black),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = false),
            ),
        )
        val fakeFlowBrands = MutableStateFlow(brandsPagingData)

        val carsPagingData = PagingData.from(
            data = listOf<Pair<Uuid, Any>>(Uuid.random() to R.drawable.batman_car),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = false),
            ),
        )
        val fakeFlowCars = MutableStateFlow(carsPagingData)

        val newsPagingData = PagingData.from(
            data = listOf(
                VideoNews(
                    id = Uuid.random(),
                    thumbnail = R.drawable.thumbnail_example,
                    name = "Test Video",
                    link = "https://example.com",
                    description = "Test description",
                    createdAt = null,
                ),
            ),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = false),
            ),
        )
        val fakeFlowNews = MutableStateFlow(newsPagingData)

        // When
        composeTestRule.setContent {
            val brandsPaged = fakeFlowBrands.collectAsLazyPagingItems()
            val newsPaged = fakeFlowNews.collectAsLazyPagingItems()
            val carsPaged = fakeFlowCars.collectAsLazyPagingItems()

            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        HomeContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brands = brandsPaged,
                            news = newsPaged,
                            recentCars = carsPaged,
                            info = HomeCallbacks.default,
                        )
                    }
                }
            }
        }

        // Then - Verify all sections are displayed
        val brandsText = context.getString(R.string.brands)
        val recentlyAddedText = context.getString(R.string.recently_added)
        val informationInterestText = context.getString(R.string.information_interest)

        composeTestRule.onNodeWithText(brandsText).assertIsDisplayed()
        composeTestRule.onNodeWithText(recentlyAddedText).assertIsDisplayed()
        composeTestRule.onNodeWithText(informationInterestText).assertIsDisplayed()
    }

    @Test
    fun homeContent_withEmptyCars_showsAddCarBanner() {
        // Given
        val brandsPagingData = PagingData.from<Pair<Uuid, Any>>(
            data = listOf(Uuid.random() to R.drawable.hot_wheels_logo_black),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = false),
            ),
        )
        val fakeFlowBrands = MutableStateFlow(brandsPagingData)

        val emptyCarsPagingData = PagingData.from<Pair<Uuid, Any>>(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        )
        val fakeFlowEmptyCars = MutableStateFlow(emptyCarsPagingData)

        val emptyNewsPagingData = PagingData.from<VideoNews>(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        )
        val fakeFlowEmptyNews = MutableStateFlow(emptyNewsPagingData)

        // When
        composeTestRule.setContent {
            val brandsPaged = fakeFlowBrands.collectAsLazyPagingItems()
            val emptyCarsPaged = fakeFlowEmptyCars.collectAsLazyPagingItems()
            val emptyNewsPaged = fakeFlowEmptyNews.collectAsLazyPagingItems()

            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        HomeContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brands = brandsPaged,
                            news = emptyNewsPaged,
                            recentCars = emptyCarsPaged,
                            info = HomeCallbacks.default,
                        )
                    }
                }
            }
        }

        // Then - Verify add car banner is displayed
        val addCarText = context.getString(R.string.add_car)
        composeTestRule.onNodeWithContentDescription(addCarText).assertIsDisplayed()
    }

    @Test
    fun homeContent_addButtonClick_triggersCallback() {
        // Given
        var addClicked = false
        val callbacks = HomeCallbacks(
            onAddClick = { addClicked = true },
            onSearchClick = {},
            onBrandClick = {},
            onCarClick = {},
            onNewsClick = {},
            onRefresh = {},
            headersCallbacks = HeaderCallbacks.default,
        )

        val emptyPagingData = PagingData.from<Pair<Uuid, Any>>(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        )
        val fakeFlowEmpty = MutableStateFlow(emptyPagingData)

        val emptyNewsPagingData = PagingData.from<VideoNews>(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        )
        val fakeFlowEmptyNews = MutableStateFlow(emptyNewsPagingData)

        // When
        composeTestRule.setContent {
            val emptyPaged = fakeFlowEmpty.collectAsLazyPagingItems()
            val emptyNewsPaged = fakeFlowEmptyNews.collectAsLazyPagingItems()

            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        HomeContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brands = emptyPaged,
                            news = emptyNewsPaged,
                            recentCars = emptyPaged,
                            info = callbacks,
                        )
                    }
                }
            }
        }

        // Then - First expand the FAB menu, then click add button
        val searchText = context.getString(R.string.search)
        // Click the main FAB to expand the menu
        composeTestRule.onNodeWithContentDescription(searchText).performClick()
        // Then click the add menu item
        val addText = context.getString(R.string.add)
        composeTestRule.onNodeWithText(addText).performClick()
        assertTrue(addClicked)
    }

    @Test
    fun homeContent_searchButtonClick_triggersCallback() {
        // Given
        var searchClicked = false
        val callbacks = HomeCallbacks(
            onAddClick = {},
            onSearchClick = { searchClicked = true },
            onBrandClick = {},
            onCarClick = {},
            onNewsClick = {},
            onRefresh = {},
            headersCallbacks = HeaderCallbacks.default,
        )

        val emptyPagingData = PagingData.from<Pair<Uuid, Any>>(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        )
        val fakeFlowEmpty = MutableStateFlow(emptyPagingData)

        val emptyNewsPagingData = PagingData.from<VideoNews>(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        )
        val fakeFlowEmptyNews = MutableStateFlow(emptyNewsPagingData)

        // When
        composeTestRule.setContent {
            val emptyPaged = fakeFlowEmpty.collectAsLazyPagingItems()
            val emptyNewsPaged = fakeFlowEmptyNews.collectAsLazyPagingItems()

            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        HomeContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brands = emptyPaged,
                            news = emptyNewsPaged,
                            recentCars = emptyPaged,
                            info = callbacks,
                        )
                    }
                }
            }
        }

        // Then - First expand the FAB menu, then click search button
        val searchText = context.getString(R.string.search)
        // Click the main FAB to expand the menu
        composeTestRule.onNodeWithContentDescription(searchText).performClick()
        // Then click the search menu item
        composeTestRule.onNodeWithText(searchText).performClick()
        assertTrue(searchClicked)
    }

    @Test
    fun homeContent_withEmptyData_displaysCorrectly() {
        // Given
        val emptyPagingData = PagingData.from<Pair<Uuid, Any>>(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        )
        val fakeFlowEmpty = MutableStateFlow(emptyPagingData)

        val emptyNewsPagingData = PagingData.from<VideoNews>(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        )
        val fakeFlowEmptyNews = MutableStateFlow(emptyNewsPagingData)

        // When
        composeTestRule.setContent {
            val emptyPaged = fakeFlowEmpty.collectAsLazyPagingItems()
            val emptyNewsPaged = fakeFlowEmptyNews.collectAsLazyPagingItems()

            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        HomeContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brands = emptyPaged,
                            news = emptyNewsPaged,
                            recentCars = emptyPaged,
                            info = HomeCallbacks.default,
                        )
                    }
                }
            }
        }

        // Then - Verify sections are still displayed even with empty data
        val brandsText = context.getString(R.string.brands)
        val recentlyAddedText = context.getString(R.string.recently_added)
        val informationInterestText = context.getString(R.string.information_interest)

        composeTestRule.onNodeWithText(brandsText).assertIsDisplayed()
        composeTestRule.onNodeWithText(recentlyAddedText).assertIsDisplayed()
        composeTestRule.onNodeWithText(informationInterestText).assertIsDisplayed()
    }
}
