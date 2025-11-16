package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

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
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GarageContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun garageContent_displaysGarageTitle() {
        // Given
        val pagingData = PagingData.from(
            data = listOf(
                CarItem(
                    model = "Test Car",
                    year = 2024,
                    manufacturer = "Test Brand",
                    brand = "Test Brand",
                    quantity = 1,
                    images = setOf(R.drawable.batman_car),
                    imageUrl = R.drawable.batman_car,
                    isFavorite = false,
                ),
            ),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = false),
            ),
        )
        val fakeFlow = MutableStateFlow(pagingData)

        // When
        composeTestRule.setContent {
            val carsPaged = fakeFlow.collectAsLazyPagingItems()
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        GarageContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            carsPaged = carsPaged,
                            topBarState = GarageTopBarState.DEFAULT,
                            searchQuery = "",
                            manufacturerList = emptyList(),
                            callbacks = GarageCallbacks.default,
                        )
                    }
                }
            }
        }

        // Then
        val garageText = context.getString(R.string.garage)
        composeTestRule.onNodeWithText(garageText).assertIsDisplayed()
    }

    @Test
    fun garageContent_displaysCarItem() {
        // Given
        val testCar = CarItem(
            model = "Ford Mustang",
            year = 2024,
            manufacturer = "HotWheels",
            brand = "HotWheels",
            quantity = 1,
            images = setOf(R.drawable.batman_car),
            imageUrl = R.drawable.batman_car,
            isFavorite = false,
        )
        val pagingData = PagingData.from(
            data = listOf(testCar),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = false),
            ),
        )
        val fakeFlow = MutableStateFlow(pagingData)

        // When
        composeTestRule.setContent {
            val carsPaged = fakeFlow.collectAsLazyPagingItems()
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        GarageContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            carsPaged = carsPaged,
                            topBarState = GarageTopBarState.DEFAULT,
                            searchQuery = "",
                            manufacturerList = emptyList(),
                            callbacks = GarageCallbacks.default,
                        )
                    }
                }
            }
        }

        // Then
        composeTestRule.onNodeWithText("Ford Mustang").assertIsDisplayed()
        val manufacturerText = context.getString(R.string.manufacture_of, "HotWheels")
        composeTestRule.onNodeWithText(manufacturerText).assertIsDisplayed()
    }

    @Test
    fun garageContent_emptyState_displaysNoDataMessage() {
        // Given
        val pagingData = PagingData.from<CarItem>(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        )
        val fakeFlow = MutableStateFlow(pagingData)

        // When
        composeTestRule.setContent {
            val carsPaged = fakeFlow.collectAsLazyPagingItems()
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        GarageContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            carsPaged = carsPaged,
                            topBarState = GarageTopBarState.DEFAULT,
                            searchQuery = "",
                            manufacturerList = emptyList(),
                            callbacks = GarageCallbacks.default,
                        )
                    }
                }
            }
        }

        // Then
        val carsNotFoundText = context.getString(R.string.cars_not_found)
        composeTestRule.onNodeWithText(carsNotFoundText).assertIsDisplayed()
    }

    @Test
    fun garageContent_addButtonClick_triggersCallback() {
        // Given
        var addClicked = false
        val pagingData = PagingData.from<CarItem>(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        )
        val fakeFlow = MutableStateFlow(pagingData)

        val callbacks = GarageCallbacks(
            onSearchQueryChange = {},
            onAddClick = { addClicked = true },
            onCarClick = {},
            onToggleFavorite = { _, _ -> },
            onRefresh = {},
            onUiStateChange = {},
            onSearchClick = {},
            filterBar = GarageCallbacks.FilterBar.default,
            headersCallbacks = HeaderCallbacks.default,
        )

        // When
        composeTestRule.setContent {
            val carsPaged = fakeFlow.collectAsLazyPagingItems()
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        GarageContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            carsPaged = carsPaged,
                            topBarState = GarageTopBarState.DEFAULT,
                            searchQuery = "",
                            manufacturerList = emptyList(),
                            callbacks = callbacks,
                        )
                    }
                }
            }
        }

        // Then
        val addText = context.getString(R.string.add)
        composeTestRule.onNodeWithContentDescription(addText).performClick()
        assertTrue(addClicked)
    }

    @Test
    fun garageContent_carItemClick_triggersCallback() {
        // Given
        var clickedCar: CarItem? = null
        val testCar = CarItem(
            model = "Test Car",
            year = 2024,
            manufacturer = "Test Brand",
            brand = "Test Brand",
            quantity = 1,
            images = setOf(R.drawable.batman_car),
            imageUrl = R.drawable.batman_car,
            isFavorite = false,
        )
        val pagingData = PagingData.from(
            data = listOf(testCar),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = false),
            ),
        )
        val fakeFlow = MutableStateFlow(pagingData)

        val callbacks = GarageCallbacks(
            onSearchQueryChange = {},
            onAddClick = {},
            onCarClick = { clickedCar = it },
            onToggleFavorite = { _, _ -> },
            onRefresh = {},
            onUiStateChange = {},
            onSearchClick = {},
            filterBar = GarageCallbacks.FilterBar.default,
            headersCallbacks = HeaderCallbacks.default,
        )

        // When
        composeTestRule.setContent {
            val carsPaged = fakeFlow.collectAsLazyPagingItems()
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        GarageContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            carsPaged = carsPaged,
                            topBarState = GarageTopBarState.DEFAULT,
                            searchQuery = "",
                            manufacturerList = emptyList(),
                            callbacks = callbacks,
                        )
                    }
                }
            }
        }

        // Then
        composeTestRule.onNodeWithText("Test Car").performClick()
        assertTrue(clickedCar == testCar)
    }
}
