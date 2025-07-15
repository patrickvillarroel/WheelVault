package io.github.patrickvillarroel.wheel.vault.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.github.patrickvillarroel.wheel.vault.ui.screen.camera.CameraLensScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand.BrandDetailScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarDetailScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit.CarEditScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.home.HomeScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.login.LoginScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.login.LoginWithEmailScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.profile.ProfileScreen

@Composable
fun WheelVaultApp(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(NavigationKeys.Login)

    SharedTransitionLayout {
        NavDisplay(
            backStack = backStack,
            modifier = modifier.fillMaxSize(),
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = {
                ContentTransform(slideInHorizontally { it }, slideOutHorizontally())
            },
            popTransitionSpec = {
                ContentTransform(slideInHorizontally(), slideOutHorizontally { it })
            },
            entryProvider = entryProvider {
                entry<NavigationKeys.Login> { _ ->
                    LoginScreen(
                        onLoginSuccess = {
                            backStack.remove(NavigationKeys.Login)
                            backStack.add(NavigationKeys.Home)
                        },
                        onLoginWithEmailAndPasswordClick = {
                            backStack.add(NavigationKeys.LoginWithEmailAndPassword(isRegister = false))
                        },
                        onRegisterClick = {
                            backStack.add(NavigationKeys.LoginWithEmailAndPassword(isRegister = true))
                        },
                    )
                }

                entry<NavigationKeys.LoginWithEmailAndPassword> { (isRegister) ->
                    LoginWithEmailScreen(
                        isRegister = isRegister,
                        onLoginSuccess = {
                            backStack.remove(NavigationKeys.Login)
                            backStack.add(NavigationKeys.Home)
                        },
                    )
                }

                entry<NavigationKeys.Home> { _ ->
                    HomeScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        onAddClick = { backStack.add(NavigationKeys.AddCamera) },
                        onSearchClick = { backStack.add(NavigationKeys.Garage("")) },
                        onBrandClick = { backStack.add(NavigationKeys.BrandDetail(it)) },
                        onGarageClick = { backStack.add(NavigationKeys.Garage()) },
                        onCarClick = { backStack.add(NavigationKeys.CarDetail(it)) },
                        onFavoritesClick = { backStack.add(NavigationKeys.Garage(favorites = true)) },
                        onStatisticsClick = { backStack.add(NavigationKeys.Garage(statistics = true)) },
                        onProfileClick = { backStack.add(NavigationKeys.Profile) },
                    )
                }

                entry<NavigationKeys.AddCamera> { _ ->
                    CameraLensScreen(
                        onBack = { backStack.removeLastOrNull() },
                        // TODO send more data like picture or save a partial in DB
                        onAddDetail = { backStack.add(NavigationKeys.CarEdit(it)) },
                    )
                }

                entry<NavigationKeys.BrandDetail> { (id) ->
                    BrandDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        brandId = id,
                        onBackClick = { backStack.removeLastOrNull() },
                        onProfileClick = { backStack.add(NavigationKeys.Profile) },
                        onGarageClick = { backStack.add(NavigationKeys.Garage()) },
                        onFavoritesClick = { backStack.add(NavigationKeys.Garage(favorites = true)) },
                        onStatisticsClick = { backStack.add(NavigationKeys.Garage(statistics = true)) },
                        onAddClick = { backStack.add(NavigationKeys.AddCamera) },
                        onCarClick = { backStack.add(NavigationKeys.CarDetail(it)) },
                    )
                }

                entry<NavigationKeys.Garage> {
                    // TODO pass the state from others screens
                    GarageScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        onProfileClick = { backStack.add(NavigationKeys.Profile) },
                        onHomeClick = {
                            val indexHome = backStack.lastIndexOf(NavigationKeys.Home)
                            if (indexHome != -1) {
                                backStack.removeAll { it != NavigationKeys.Home }
                            } else {
                                backStack.add(NavigationKeys.Home)
                            }
                        },
                        onCarClick = { backStack.add(NavigationKeys.CarDetail(it)) },
                        onAddClick = { backStack.add(NavigationKeys.AddCamera) },
                    )
                }

                entry<NavigationKeys.CarDetail> { (id) ->
                    CarDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        carId = id,
                        onBackClick = { backStack.removeLastOrNull() },
                        onProfileClick = { backStack.add(NavigationKeys.Profile) },
                        onGarageClick = { backStack.add(NavigationKeys.Garage()) },
                        onFavoritesClick = { backStack.add(NavigationKeys.Garage(favorites = true)) },
                        onStatisticsClick = { backStack.add(NavigationKeys.Garage(statistics = true)) },
                        onEditClick = { backStack.add(it.toCarEdit()) },
                    )
                }

                entry<NavigationKeys.CarEdit> { edit ->
                    CarEditScreen(
                        partialCarItem = edit.toCarPartial(),
                        onBackClick = { backStack.removeLastOrNull() },
                        onProfileClick = { backStack.add(NavigationKeys.Profile) },
                        onGarageClick = { backStack.add(NavigationKeys.Garage()) },
                        onFavoritesClick = { backStack.add(NavigationKeys.Garage(favorites = true)) },
                        onStatisticsClick = { backStack.add(NavigationKeys.Garage(statistics = true)) },
                    )
                }

                entry<NavigationKeys.Profile> { _ ->
                    ProfileScreen(
                        onBackClick = { backStack.removeLastOrNull() },
                        onGarageClick = { backStack.add(NavigationKeys.Garage()) },
                        onFavoritesClick = { backStack.add(NavigationKeys.Garage(favorites = true)) },
                        onStatisticsClick = { backStack.add(NavigationKeys.Garage(statistics = true)) },
                    )
                }
            },
        )
    }
}
