package io.github.patrickvillarroel.wheel.vault.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.github.patrickvillarroel.wheel.vault.ui.screen.home.CollectorsHomeScreen

@Composable
fun WheelVaultApp(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(NavigationKeys.Home)

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry(NavigationKeys.Home) {
                CollectorsHomeScreen()
            }
        },
    )
}
