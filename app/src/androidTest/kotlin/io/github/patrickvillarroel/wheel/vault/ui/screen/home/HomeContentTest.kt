package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme
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
        val brands = mapOf(Uuid.random() to R.drawable.hot_wheels_logo_black)
        val recentCars = mapOf(Uuid.random() to R.drawable.batman_car)
        val news = listOf(
            VideoNews(
                id = Uuid.random(),
                thumbnail = R.drawable.thumbnail_example,
                name = "Test Video",
                link = "https://example.com",
                description = "Test description",
                createdAt = null,
            ),
        )

        // When
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        HomeContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brands = brands,
                            news = news,
                            recentCars = recentCars,
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
        val brands = mapOf(Uuid.random() to R.drawable.hot_wheels_logo_black)
        val emptyRecentCars = emptyMap<Uuid, Any>()

        // When
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        HomeContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brands = brands,
                            news = emptyList(),
                            recentCars = emptyRecentCars,
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

        // When
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        HomeContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brands = emptyMap(),
                            news = emptyList(),
                            recentCars = emptyMap(),
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

        // When
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        HomeContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brands = emptyMap(),
                            news = emptyList(),
                            recentCars = emptyMap(),
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
        // When
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        HomeContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brands = emptyMap(),
                            news = emptyList(),
                            recentCars = emptyMap(),
                            info = HomeCallbacks.default,
                        )
                    }
                }
            }
        }

        // Then - Verify sections are still displayed even with empty data
        val brandsText = context.getString(R.string.brands)
        val recentlyAddedText = context.getString(R.string.recently_added)

        composeTestRule.onNodeWithText(brandsText).assertIsDisplayed()
        composeTestRule.onNodeWithText(recentlyAddedText).assertIsDisplayed()
    }
}
