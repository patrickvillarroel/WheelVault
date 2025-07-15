package io.github.patrickvillarroel.wheel.vault.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface NavigationKeys {
    @Serializable
    data object Home : NavKey

    @Serializable
    data object AddCamera : NavKey

    @Serializable
    data class BrandDetail(val id: Int) : NavKey

    @Serializable
    data class CarDetail(val id: Int) : NavKey

    @Serializable
    data class Garage(val query: String? = null, val favorites: Boolean = false, val statistics: Boolean = false) :
        NavKey

    @Serializable
    data class CarEdit(
        val model: String? = null,
        val year: Int? = null,
        val manufacturer: String? = null,
        val quantity: Int = 0,
        val brand: String? = null,
        val description: String? = null,
        val category: String? = null,
        val images: List<String> = emptyList(),
        val isFavorite: Boolean = false,
    ) : NavKey

    @Serializable
    data object Login : NavKey

    @Serializable
    data class LoginWithEmail(val isRegister: Boolean) : NavKey
}
