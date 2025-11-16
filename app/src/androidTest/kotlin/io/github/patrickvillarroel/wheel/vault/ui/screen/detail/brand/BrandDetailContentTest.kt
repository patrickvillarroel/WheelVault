package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.uuid.Uuid

class BrandDetailContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun brandDetailContent_displaysBrandInformation() {
        // Given
        val testBrand = Brand(
            id = Uuid.random(),
            name = "HotWheels",
            description = "Premium die-cast cars and tracks",
            image = R.drawable.hot_wheels_logo_black,
            contentDescription = "HotWheels logo",
            createdAt = null,
        )
        val brandDetail = BrandDetail(
            brand = testBrand,
            carCollection = emptyList(),
            onCarClick = {},
            onFavoriteToggle = { _, _ -> },
            onAddClick = {},
            headerBackCallbacks = HeaderBackCallbacks.default,
            animationKey = "brand-${testBrand.id}",
        )

        // When
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        BrandDetailContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brandDetail = brandDetail,
                        )
                    }
                }
            }
        }

        // Then - Verify brand information is displayed
        val brandInfoHeader = context.getString(R.string.info_of, "HotWheels")
        composeTestRule.onNodeWithText(brandInfoHeader).assertIsDisplayed()
        composeTestRule.onNodeWithText("Premium die-cast cars and tracks", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun brandDetailContent_displaysCarCollection() {
        // Given
        val testBrand = Brand(
            id = Uuid.random(),
            name = "HotWheels",
            description = "Test description",
            image = R.drawable.hot_wheels_logo_black,
            contentDescription = null,
            createdAt = null,
        )
        val testCars = listOf(
            CarItem(
                model = "Ford Mustang",
                year = 2024,
                manufacturer = "HotWheels",
                brand = "HotWheels",
                quantity = 1,
                images = setOf(R.drawable.batman_car),
                imageUrl = R.drawable.batman_car,
                isFavorite = false,
            ),
            CarItem(
                model = "Chevrolet Corvette",
                year = 2023,
                manufacturer = "HotWheels",
                brand = "HotWheels",
                quantity = 2,
                images = setOf(R.drawable.batman_car),
                imageUrl = R.drawable.batman_car,
                isFavorite = true,
            ),
        )
        val brandDetail = BrandDetail(
            brand = testBrand,
            carCollection = testCars,
            onCarClick = {},
            onFavoriteToggle = { _, _ -> },
            onAddClick = {},
            headerBackCallbacks = HeaderBackCallbacks.default,
            animationKey = "brand-${testBrand.id}",
        )

        // When
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        BrandDetailContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brandDetail = brandDetail,
                        )
                    }
                }
            }
        }

        // Then - Verify cars are displayed
        composeTestRule.onNodeWithText("Ford Mustang").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chevrolet Corvette").assertIsDisplayed()
    }

    @Test
    fun brandDetailContent_displaysCarInCollectionHeader() {
        // Given
        val testBrand = Brand(
            id = Uuid.random(),
            name = "MiniGT",
            description = "Test description",
            image = R.drawable.hot_wheels_logo_black,
            contentDescription = null,
            createdAt = null,
        )
        val brandDetail = BrandDetail(
            brand = testBrand,
            carCollection = emptyList(),
            onCarClick = {},
            onFavoriteToggle = { _, _ -> },
            onAddClick = {},
            headerBackCallbacks = HeaderBackCallbacks.default,
            animationKey = "brand-${testBrand.id}",
        )

        // When
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        BrandDetailContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brandDetail = brandDetail,
                        )
                    }
                }
            }
        }

        // Then
        val carInCollectionText = context.getString(R.string.car_in_collection)
        composeTestRule.onNodeWithText(carInCollectionText).assertIsDisplayed()
    }

    @Test
    fun brandDetailContent_addButtonClick_triggersCallback() {
        // Given
        var addClicked = false
        val testBrand = Brand(
            id = Uuid.random(),
            name = "HotWheels",
            description = "Test description",
            image = R.drawable.hot_wheels_logo_black,
            contentDescription = null,
            createdAt = null,
        )
        val brandDetail = BrandDetail(
            brand = testBrand,
            carCollection = emptyList(),
            onCarClick = {},
            onFavoriteToggle = { _, _ -> },
            onAddClick = { addClicked = true },
            headerBackCallbacks = HeaderBackCallbacks.default,
            animationKey = "brand-${testBrand.id}",
        )

        // When
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        BrandDetailContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brandDetail = brandDetail,
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
    fun brandDetailContent_carItemClick_triggersCallback() {
        // Given
        var clickedCarId: Uuid? = null
        val testBrand = Brand(
            id = Uuid.random(),
            name = "HotWheels",
            description = "Test description",
            image = R.drawable.hot_wheels_logo_black,
            contentDescription = null,
            createdAt = null,
        )
        val testCarId = Uuid.random()
        val testCars = listOf(
            CarItem(
                id = testCarId,
                model = "Test Car",
                year = 2024,
                manufacturer = "HotWheels",
                brand = "HotWheels",
                quantity = 1,
                images = setOfNotNull(R.drawable.batman_car),
                isFavorite = false,
            ),
        )
        val brandDetail = BrandDetail(
            brand = testBrand,
            carCollection = testCars,
            onCarClick = { clickedCarId = it },
            onFavoriteToggle = { _, _ -> },
            onAddClick = {},
            headerBackCallbacks = HeaderBackCallbacks.default,
            animationKey = "brand-${testBrand.id}",
        )

        // When
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        BrandDetailContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brandDetail = brandDetail,
                        )
                    }
                }
            }
        }

        // Then
        composeTestRule.onNodeWithText("Test Car").performClick()
        assertTrue(clickedCarId == testCarId)
    }

    @Test
    fun brandDetailContent_emptyCarCollection_stillDisplaysHeader() {
        // Given
        val testBrand = Brand(
            id = Uuid.random(),
            name = "HotWheels",
            description = "Test description",
            image = R.drawable.hot_wheels_logo_black,
            contentDescription = null,
            createdAt = null,
        )
        val brandDetail = BrandDetail(
            brand = testBrand,
            carCollection = emptyList(),
            onCarClick = {},
            onFavoriteToggle = { _, _ -> },
            onAddClick = {},
            headerBackCallbacks = HeaderBackCallbacks.default,
            animationKey = "brand-${testBrand.id}",
        )

        // When
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        BrandDetailContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brandDetail = brandDetail,
                        )
                    }
                }
            }
        }

        // Then - Headers should still be displayed
        val infoOfText = context.getString(R.string.info_of, "HotWheels")
        val carInCollectionText = context.getString(R.string.car_in_collection)

        composeTestRule.onNodeWithText(infoOfText).assertIsDisplayed()
        composeTestRule.onNodeWithText(carInCollectionText).assertIsDisplayed()
    }

    @Test
    fun brandDetailContent_favoriteToggle_triggersCallback() {
        // Given
        var favoriteToggled = false
        val testBrand = Brand(
            id = Uuid.random(),
            name = "HotWheels",
            description = "Test description",
            image = R.drawable.hot_wheels_logo_black,
            contentDescription = null,
            createdAt = null,
        )
        val testCars = listOf(
            CarItem(
                model = "Test Car",
                year = 2024,
                manufacturer = "HotWheels",
                brand = "HotWheels",
                quantity = 1,
                images = setOf(R.drawable.batman_car),
                imageUrl = R.drawable.batman_car,
                isFavorite = false,
            ),
        )
        val brandDetail = BrandDetail(
            brand = testBrand,
            carCollection = testCars,
            onCarClick = {},
            onFavoriteToggle = { _, _ -> favoriteToggled = true },
            onAddClick = {},
            headerBackCallbacks = HeaderBackCallbacks.default,
            animationKey = "brand-${testBrand.id}",
        )

        // When
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    WheelVaultTheme {
                        BrandDetailContent(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            brandDetail = brandDetail,
                        )
                    }
                }
            }
        }

        // Then
        val favoritesText = context.getString(R.string.favorites)
        composeTestRule.onNodeWithContentDescription(favoritesText).performClick()
        assertTrue(favoriteToggled)
    }
}
