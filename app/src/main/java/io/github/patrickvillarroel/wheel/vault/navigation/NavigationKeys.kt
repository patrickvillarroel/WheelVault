package io.github.patrickvillarroel.wheel.vault.navigation

import androidx.navigation3.runtime.NavKey

sealed interface NavigationKeys {
    data object Home : NavKey
}
