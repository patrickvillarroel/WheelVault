@file:OptIn(ExperimentalUuidApi::class)

package io.github.patrickvillarroel.wheel.vault.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.github.patrickvillarroel.wheel.vault.ui.screen.camera.CameraLensScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand.BrandDetailScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarDetailScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit.CarEditScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.home.HomeNavCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.home.HomeScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.login.LoginScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.login.LoginWithEmailScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.profile.ProfileScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.session.SessionUiStatus
import io.github.patrickvillarroel.wheel.vault.ui.screen.session.SessionViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.splash.OnboardingScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.splash.OnboardingViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.splash.SplashScreen
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@Composable
fun WheelVaultApp(
    modifier: Modifier = Modifier,
    sessionViewModel: SessionViewModel = koinViewModel(),
    onboardingViewModel: OnboardingViewModel = koinViewModel(),
) {
    val session by sessionViewModel.session.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.CREATED)
    val onboardingState by onboardingViewModel.uiState.collectAsStateWithLifecycle(
        minActiveState = Lifecycle.State.CREATED,
    )
    val backStack = rememberNavBackStack(NavigationKeys.Splash)
    var isSplashDone by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(session, isSplashDone) {
        if (!isSplashDone) return@LaunchedEffect

        when (session) {
            is SessionUiStatus.Authenticated -> {
                if (backStack.lastOrNull() is NavigationKeys.LoginWithEmailAndPassword ||
                    backStack.lastOrNull() is NavigationKeys.Login ||
                    backStack.lastOrNull() is NavigationKeys.Splash ||
                    backStack.lastOrNull() is NavigationKeys.Onboarding
                ) {
                    backStack.clear()
                    backStack.add(NavigationKeys.Home)
                }
            }

            SessionUiStatus.NotAuthenticated,
            SessionUiStatus.RefreshFailure,
            -> {
                backStack.clear()
                backStack.add(NavigationKeys.Login)
            }

            SessionUiStatus.Initializing -> Unit
        }
    }

    SharedTransitionLayout {
        NavDisplay(
            backStack = backStack,
            modifier = modifier.fillMaxSize(),
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = { ContentTransform(slideInHorizontally { it }, slideOutHorizontally()) },
            popTransitionSpec = { ContentTransform(slideInHorizontally(), slideOutHorizontally { it }) },
            entryProvider = entryProvider {
                entry<NavigationKeys.Splash>(
                    transitionSpec = { ContentTransform(slideInVertically { -it }, slideOutVertically { it }) },
                    popTransitionSpec = { ContentTransform(slideInVertically { it }, slideOutVertically { -it }) },
                ) { _ ->
                    SplashScreen(onVideoFinish = {
                        if (onboardingState !is OnboardingViewModel.OnboardingUiState.Success) {
                            backStack.add(NavigationKeys.Onboarding)
                            return@SplashScreen
                        }
                        isSplashDone = true
                    })
                }

                entry<NavigationKeys.Login> { _ ->
                    LoginScreen(
                        onLoginSuccess = {
                            backStack.remove(NavigationKeys.Login)
                            backStack.add(NavigationKeys.Onboarding)
                        },
                        onLoginWithEmail = {
                            backStack.add(NavigationKeys.LoginWithEmailAndPassword(isMagicLink = true))
                        },
                        onLoginWithEmailAndPasswordClick = {
                            backStack.add(NavigationKeys.LoginWithEmailAndPassword(isRegister = false))
                        },
                        onRegisterClick = {
                            backStack.add(NavigationKeys.LoginWithEmailAndPassword(isRegister = true))
                        },
                    )
                }

                entry<NavigationKeys.LoginWithEmailAndPassword> { (isRegister, isMagicLink) ->
                    LoginWithEmailScreen(
                        isRegister = isRegister,
                        isMagicLink = isMagicLink,
                        onLoginSuccess = {
                            backStack.remove(NavigationKeys.Login)
                            backStack.add(NavigationKeys.Home)
                        },
                    )
                }

                entry<NavigationKeys.Onboarding> { _ ->
                    OnboardingScreen(onFinish = { isSplashDone = true })
                }

                entry<NavigationKeys.Home> { _ ->
                    HomeScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        callbacks = HomeNavCallbacks(
                            onAddClick = { backStack.add(NavigationKeys.AddCamera) },
                            onSearchClick = { backStack.add(NavigationKeys.Garage("")) },
                            onBrandClick = { backStack.add(NavigationKeys.BrandDetail(it.toKotlinUuid())) },
                            onGarageClick = { backStack.add(NavigationKeys.Garage()) },
                            onCarClick = { backStack.add(NavigationKeys.CarDetail(it.toKotlinUuid())) },
                            onFavoritesClick = { backStack.add(NavigationKeys.Garage(favorites = true)) },
                            onStatisticsClick = { backStack.add(NavigationKeys.Garage(statistics = true)) },
                            onProfileClick = { backStack.add(NavigationKeys.Profile) },
                        ),
                    )
                }

                entry<NavigationKeys.AddCamera> { _ ->
                    CameraLensScreen(
                        onBack = { backStack.removeLastOrNull() },
                        // TODO send more data like picture or save a partial in DB
                        onAddDetail = { backStack.add(NavigationKeys.CarEdit(model = it)) },
                    )
                }

                entry<NavigationKeys.BrandDetail> { (id) ->
                    BrandDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        brandId = id.toJavaUuid(),
                        headerBackCallbacks = HeaderBackCallbacks(
                            onBackClick = { backStack.removeLastOrNull() },
                            onProfileClick = { backStack.add(NavigationKeys.Profile) },
                            onGarageClick = { backStack.add(NavigationKeys.Garage()) },
                            onFavoritesClick = { backStack.add(NavigationKeys.Garage(favorites = true)) },
                            onStatisticsClick = { backStack.add(NavigationKeys.Garage(statistics = true)) },
                        ),
                        onAddClick = { backStack.add(NavigationKeys.AddCamera) },
                        onCarClick = { backStack.add(NavigationKeys.CarDetail(it.toKotlinUuid())) },
                    )
                }

                entry<NavigationKeys.Garage> {
                    // TODO pass the state from others screens
                    GarageScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        onHomeClick = { backStack.removeAllOrAdd(NavigationKeys.Home) },
                        onCarClick = { backStack.add(NavigationKeys.CarDetail(it.toKotlinUuid())) },
                        onAddClick = { backStack.add(NavigationKeys.AddCamera) },
                        onProfileClick = { backStack.add(NavigationKeys.Profile) },
                    )
                }

                entry<NavigationKeys.CarDetail> { (id) ->
                    CarDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        carId = id.toJavaUuid(),
                        onEditClick = { backStack.add(it.toCarEdit()) },
                        headerBackCallbacks = HeaderBackCallbacks(
                            onBackClick = { backStack.removeLastOrNull() },
                            onProfileClick = { backStack.add(NavigationKeys.Profile) },
                            onGarageClick = { backStack.add(NavigationKeys.Garage()) },
                            onFavoritesClick = { backStack.add(NavigationKeys.Garage(favorites = true)) },
                            onStatisticsClick = { backStack.add(NavigationKeys.Garage(statistics = true)) },
                        ),
                    )
                }

                entry<NavigationKeys.CarEdit> { edit ->
                    CarEditScreen(
                        partialCarItem = edit.toCarPartial(),
                        headersBackCallbacks = HeaderBackCallbacks(
                            onBackClick = { backStack.removeLastOrNull() },
                            onProfileClick = { backStack.add(NavigationKeys.Profile) },
                            onGarageClick = { backStack.add(NavigationKeys.Garage()) },
                            onFavoritesClick = { backStack.add(NavigationKeys.Garage(favorites = true)) },
                            onStatisticsClick = { backStack.add(NavigationKeys.Garage(statistics = true)) },
                        ),
                    )
                }

                entry<NavigationKeys.Profile> { _ ->
                    ProfileScreen(
                        backCallbacks = HeaderBackCallbacks(
                            onProfileClick = {},
                            onBackClick = { backStack.removeLastOrNull() },
                            onGarageClick = { backStack.add(NavigationKeys.Garage()) },
                            onFavoritesClick = { backStack.add(NavigationKeys.Garage(favorites = true)) },
                            onStatisticsClick = { backStack.add(NavigationKeys.Garage(statistics = true)) },
                        ),
                    )
                }
            },
        )
    }
}
