package io.github.patrickvillarroel.wheel.vault.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.github.patrickvillarroel.wheel.vault.ui.screen.camera.CameraLensScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand.BrandDetailScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.home.HomeScreen

@Composable
fun WheelVaultApp(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(NavigationKeys.Home)

    NavDisplay(
        modifier = modifier.fillMaxSize(),
        backStack = backStack,
        entryProvider = entryProvider {
            entry<NavigationKeys.Home> {
                HomeScreen(
                    onAddClick = { backStack.add(NavigationKeys.AddCamera) },
                    onSearchClick = { backStack.add(NavigationKeys.Garage("")) },
                    onBrandClick = { backStack.add(NavigationKeys.BrandDetail(it)) },
                    onGarageClick = { backStack.add(NavigationKeys.Garage()) },
                    onFavoritesClick = { backStack.add(NavigationKeys.Garage(favorites = true)) },
                    onStatisticsClick = { backStack.add(NavigationKeys.Garage(statistics = true)) },
                    onProfileClick = { /* TODO add profile screen */ },
                )
            }

            entry<NavigationKeys.AddCamera> {
                CameraLensScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onAddDetail = { /* TODO add detail create car screen */ },
                )
            }

            entry<NavigationKeys.BrandDetail> { (id) ->
                BrandDetailScreen(
                    id,
                    onBackClick = { backStack.removeLastOrNull() },
                    onProfileClick = { /* TODO add profile screen */ },
                    onGarageClick = { backStack.add(NavigationKeys.Garage()) },
                    onFavoritesClick = { backStack.add(NavigationKeys.Garage(favorites = true)) },
                    onStatisticsClick = { backStack.add(NavigationKeys.Garage(statistics = true)) },
                    onAddClick = { backStack.add(NavigationKeys.AddCamera) },
                )
            }

            entry<NavigationKeys.Garage> {
                // TODO pass the state from others screens
                GarageScreen(
                    onProfileClick = { /* TODO add profile */ },
                    onHomeClick = {
                        val indexHome = backStack.lastIndexOf(NavigationKeys.Home)
                        if (indexHome != -1) {
                            backStack.removeAll { it != NavigationKeys.Home }
                        } else {
                            backStack.add(NavigationKeys.Home)
                        }
                    },
                    onAddClick = { backStack.add(NavigationKeys.AddCamera) },
                )
            }
        },
    )
}
