@file:OptIn(ExperimentalUuidApi::class)

package io.github.patrickvillarroel.wheel.vault.navigation

import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object NavigationKeys {
    @Serializable
    data object Splash : NavKey

    @Serializable
    data object Onboarding : NavKey

    @Serializable
    data object Home : NavKey

    @Serializable
    data object AddCamera : NavKey

    @Serializable
    data class BrandDetail(val id: Uuid) : NavKey

    @Serializable
    data class CarDetail(val id: Uuid) : NavKey

    @Serializable
    data class Garage(val query: String? = null, val favorites: Boolean = false, val statistics: Boolean = false) :
        NavKey

    @Serializable
    data class Exchanges(val query: String? = null) : NavKey

    @Serializable
    data class ExchangeCarDetail(val id: Uuid) : NavKey

    @Serializable
    data object ExchangeCarSelection : NavKey

    @Serializable
    data class ExchangeCarOffer(val id: Uuid) : NavKey

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
    ) : NavKey

    @Serializable
    data object Login : NavKey

    @Serializable
    data class LoginWithEmailAndPassword(val isRegister: Boolean = false, val isMagicLink: Boolean = false) : NavKey

    @Serializable
    data object Profile : NavKey
}
