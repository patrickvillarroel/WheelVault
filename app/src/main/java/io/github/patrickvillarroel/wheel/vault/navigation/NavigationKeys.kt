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
    data class Garage(val query: String? = null, val favorites: Boolean = false, val statistics: Boolean = false) :
        NavKey
}
