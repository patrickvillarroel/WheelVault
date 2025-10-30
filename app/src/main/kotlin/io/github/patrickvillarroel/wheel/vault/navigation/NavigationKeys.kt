@file:OptIn(ExperimentalUuidApi::class)

package io.github.patrickvillarroel.wheel.vault.navigation

import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** All screens to navigate */
@Serializable
sealed class NavigationKeys : NavKey {
    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.splash.SplashScreen] */
    @Serializable
    data object Splash : NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.splash.OnboardingScreen] */
    @Serializable
    data object Onboarding : NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.home.HomeScreen] */
    @Serializable
    data object Home : NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.camera.CameraLensScreen] */
    @Serializable
    data object AddCamera : NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand.BrandDetailScreen] */
    @Serializable
    data class BrandDetail(val id: Uuid) : NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarDetailScreen] */
    @Serializable
    data class CarDetail(val id: Uuid) : NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageScreen] */
    @Serializable
    data class Garage(val query: String? = null, val favorites: Boolean = false, val statistics: Boolean = false) :
        NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.garage.ExchangeScreen] */
    @Serializable
    data class Exchanges(val query: String? = null) : NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.detail.ExchangeCarDetailScreen] */
    @Serializable
    data class ExchangeCarDetail(val id: Uuid) : NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.selection.ExchangeCarSelectionScreen] */
    @Serializable
    data object ExchangeCarSelection : NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.offer.ExchangeCarOfferScreen] */
    @Serializable
    data class ExchangeCarOffer(val id: Uuid) : NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.confirmation.ExchangeConfirmCarScreen] */
    @Serializable
    data class ExchangeConfirmation(val id: Uuid) : NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit.CarEditScreen] */
    @Stable
    @Serializable
    data class CarEdit(
        val model: String? = null,
        val year: Int? = null,
        val manufacturer: String? = null,
        val quantity: Int = 0,
        val brand: String? = null,
        val description: String? = null,
        val category: String? = null,
        val images: Set<String> = emptySet(),
        val isFavorite: Boolean = false,
        val id: Uuid? = null,
        val fromCamera: Boolean = false,
    ) : NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.login.LoginScreen] */
    @Serializable
    data object Login : NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.login.LoginWithEmailScreen] */
    @Serializable
    data class LoginWithEmailAndPassword(val isRegister: Boolean = false, val isMagicLink: Boolean = false) :
        NavigationKeys()

    /** Refers to [io.github.patrickvillarroel.wheel.vault.ui.screen.profile.ProfileScreen] */
    @Serializable
    data object Profile : NavigationKeys()
}
