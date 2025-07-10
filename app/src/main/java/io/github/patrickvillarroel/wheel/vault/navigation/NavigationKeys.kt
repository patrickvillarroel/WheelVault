package io.github.patrickvillarroel.wheel.vault.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface NavigationKeys {
    @Serializable
    data object Home : NavKey

    @Serializable
    data object AddCamera : NavKey
}
