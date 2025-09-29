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
import androidx.compose.runtime.snapshots.Snapshot
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
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.confirmation.ExchangeConfirmCarScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.detail.ExchangeCarDetailScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.garage.ExchangeScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.offer.ExchangeCarOfferScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.selection.ExchangeCarSelectionScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageCallbacks
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
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun WheelVaultApp(
    modifier: Modifier = Modifier,
    sessionViewModel: SessionViewModel = koinActivityViewModel(),
    onboardingViewModel: OnboardingViewModel = koinActivityViewModel(),
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
                    Snapshot.withMutableSnapshot {
                        if (!backStack.contains(NavigationKeys.Home)) backStack += NavigationKeys.Home
                        backStack.removeAll { it !is NavigationKeys.Home }
                    }
                }
            }

            SessionUiStatus.NotAuthenticated,
            SessionUiStatus.RefreshFailure,
            -> {
                Snapshot.withMutableSnapshot {
                    onboardingViewModel.reloadOnboardingState()
                    if (!backStack.contains(NavigationKeys.Login)) backStack += NavigationKeys.Login
                    backStack.removeAll { it !is NavigationKeys.Login }
                }
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
                        if (onboardingState is OnboardingViewModel.OnboardingUiState.Uncompleted) {
                            backStack += NavigationKeys.Onboarding
                            return@SplashScreen
                        }
                        isSplashDone = true
                    })
                }

                entry<NavigationKeys.Login> { _ ->
                    LoginScreen(
                        onLoginSuccess = {
                            Snapshot.withMutableSnapshot {
                                backStack += NavigationKeys.Home
                                backStack -= NavigationKeys.Login
                            }
                        },
                        onLoginWithEmail = {
                            backStack += NavigationKeys.LoginWithEmailAndPassword(isMagicLink = true)
                        },
                        onLoginWithEmailAndPasswordClick = {
                            backStack += NavigationKeys.LoginWithEmailAndPassword(isRegister = false)
                        },
                        onRegisterClick = {
                            backStack += NavigationKeys.LoginWithEmailAndPassword(isRegister = true)
                        },
                        loginViewModel = koinActivityViewModel(),
                    )
                }

                entry<NavigationKeys.LoginWithEmailAndPassword> {
                    val (isRegister, isMagicLink) = it
                    LoginWithEmailScreen(
                        isRegister = isRegister,
                        isMagicLink = isMagicLink,
                        onLoginSuccess = {
                            Snapshot.withMutableSnapshot {
                                backStack += NavigationKeys.Home
                                backStack -= it
                            }
                        },
                        loginViewModel = koinActivityViewModel(),
                    )
                }

                entry<NavigationKeys.Onboarding> { _ ->
                    OnboardingScreen(onFinish = {
                        isSplashDone = true
                        backStack -= NavigationKeys.Onboarding
                    }, viewModel = koinActivityViewModel())
                }

                entry<NavigationKeys.Home> { _ ->
                    HomeScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        callbacks = HomeNavCallbacks(
                            onAddClick = { backStack += NavigationKeys.AddCamera },
                            onSearchClick = { backStack += NavigationKeys.Garage("") },
                            onBrandClick = { backStack += NavigationKeys.BrandDetail(it) },
                            onGarageClick = { backStack += NavigationKeys.Garage() },
                            onCarClick = { backStack += NavigationKeys.CarDetail(it) },
                            onFavoritesClick = { backStack += NavigationKeys.Garage(favorites = true) },
                            onStatisticsClick = { backStack += NavigationKeys.Garage(statistics = true) },
                            onProfileClick = { backStack += NavigationKeys.Profile },
                            onExchangesClick = { backStack += NavigationKeys.Exchanges() },
                        ),
                    )
                }

                entry<NavigationKeys.AddCamera> { _ ->
                    CameraLensScreen(
                        onBack = { backStack.removeLastOrNull() },
                        onAddDetail = { carModel, _ ->
                            Snapshot.withMutableSnapshot {
                                backStack += NavigationKeys.CarEdit(model = carModel, fromCamera = true)
                                backStack -= NavigationKeys.AddCamera
                            }
                        },
                    )
                }

                entry<NavigationKeys.BrandDetail> { (id) ->
                    BrandDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        brandId = id,
                        headerBackCallbacks = HeaderBackCallbacks(
                            onBackClick = { backStack.removeLastOrNull() },
                            onProfileClick = { backStack += NavigationKeys.Profile },
                            onGarageClick = { backStack += NavigationKeys.Garage() },
                            onFavoritesClick = { backStack += NavigationKeys.Garage(favorites = true) },
                            onStatisticsClick = { backStack += NavigationKeys.Garage(statistics = true) },
                            onExchangesClick = { backStack += NavigationKeys.Exchanges() },
                        ),
                        onAddClick = { backStack += NavigationKeys.AddCamera },
                        onCarClick = { backStack += NavigationKeys.CarDetail(it) },
                    )
                }

                entry<NavigationKeys.Garage> { (query, favorites) ->
                    GarageScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        query = query ?: "",
                        favorites = favorites,
                        callbacks = GarageCallbacks.Partial(
                            onHomeClick = { backStack.removeAllOrAdd(NavigationKeys.Home) },
                            onCarClick = { backStack += NavigationKeys.CarDetail(it) },
                            onAddClick = { backStack += NavigationKeys.AddCamera },
                            onProfileClick = { backStack += NavigationKeys.Profile },
                            onExchangesClick = { backStack += NavigationKeys.Exchanges() },
                        ),
                    )
                }

                entry<NavigationKeys.CarDetail> { (id) ->
                    CarDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        carId = id,
                        onEditClick = { backStack += it.toCarEdit() },
                        headerBackCallbacks = HeaderBackCallbacks(
                            onBackClick = { backStack.removeLastOrNull() },
                            onProfileClick = { backStack += NavigationKeys.Profile },
                            onGarageClick = { backStack += NavigationKeys.Garage() },
                            onFavoritesClick = { backStack += NavigationKeys.Garage(favorites = true) },
                            onStatisticsClick = { backStack += NavigationKeys.Garage(statistics = true) },
                            onExchangesClick = { backStack += NavigationKeys.Exchanges() },
                        ),
                    )
                }

                entry<NavigationKeys.CarEdit> { edit ->
                    CarEditScreen(
                        partialCarItem = edit.toCarPartial(),
                        fromCamera = edit.fromCamera,
                        headersBackCallbacks = HeaderBackCallbacks(
                            onBackClick = { backStack.removeLastOrNull() },
                            onProfileClick = { backStack += NavigationKeys.Profile },
                            onGarageClick = { backStack += NavigationKeys.Garage() },
                            onFavoritesClick = { backStack += NavigationKeys.Garage(favorites = true) },
                            onStatisticsClick = { backStack += NavigationKeys.Garage(statistics = true) },
                            onExchangesClick = { backStack += NavigationKeys.Exchanges() },
                        ),
                    )
                }

                entry<NavigationKeys.Profile> { _ ->
                    ProfileScreen(
                        backCallbacks = HeaderBackCallbacks(
                            onProfileClick = {},
                            onBackClick = { backStack.removeLastOrNull() },
                            onGarageClick = { backStack += NavigationKeys.Garage() },
                            onFavoritesClick = { backStack += NavigationKeys.Garage(favorites = true) },
                            onStatisticsClick = { backStack += NavigationKeys.Garage(statistics = true) },
                            onExchangesClick = { backStack += NavigationKeys.Exchanges() },
                        ),
                        sessionViewModel = koinActivityViewModel(),
                    )
                }

                entry<NavigationKeys.Exchanges> { (query) ->
                    ExchangeScreen(
                        query = query ?: "",
                        callbacks = GarageCallbacks.Partial(
                            onHomeClick = { backStack.removeAllOrAdd(NavigationKeys.Home) },
                            onCarClick = { backStack += NavigationKeys.ExchangeCarDetail(it) },
                            onAddClick = { backStack += NavigationKeys.AddCamera },
                            onProfileClick = { backStack += NavigationKeys.Profile },
                            onExchangesClick = {},
                        ),
                    )
                }

                entry<NavigationKeys.ExchangeCarDetail> { (id) ->
                    ExchangeCarDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        carId = id,
                        onExchangeCarClick = { backStack += NavigationKeys.ExchangeCarSelection },
                    )
                }

                entry<NavigationKeys.ExchangeCarSelection> { _ ->
                    ExchangeCarSelectionScreen(
                        onCarClick = { backStack += NavigationKeys.ExchangeCarOffer(it.id) },
                    )
                }

                entry<NavigationKeys.ExchangeCarOffer> { (id) ->
                    ExchangeCarOfferScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        carId = id,
                        onExchangeTemporalClick = { backStack += NavigationKeys.ExchangeConfirmation(it) },
                    )
                }

                entry<NavigationKeys.ExchangeConfirmation> { (id) ->
                    ExchangeConfirmCarScreen(id)
                }
            },
        )
    }
}
