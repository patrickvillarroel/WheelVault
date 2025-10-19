package io.github.patrickvillarroel.wheel.vault.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent
import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
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

/** Entry point of app with navigation */
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
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = { ContentTransform(slideInHorizontally { it }, slideOutHorizontally()) },
            popTransitionSpec = { ContentTransform(slideInHorizontally(), slideOutHorizontally { it }) },
        ) { key ->
            if (key !is NavigationKeys) {
                navigationLogger.e { "Unexpected key type: $key" }
                return@NavDisplay entry(key) {
                    Scaffold(Modifier.fillMaxSize()) { paddingValues ->
                        Column(
                            Modifier.padding(paddingValues),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text("Unknown screen $key")
                            TextButton(onClick = {
                                Snapshot.withMutableSnapshot {
                                    backStack.add(NavigationKeys.Home)
                                    backStack.remove(key)
                                }
                            }) {
                                Text("Go back")
                            }
                        }
                    }
                }
            }

            when (key) {
                is NavigationKeys.Splash -> entry(
                    key,
                    transitionSpec = { ContentTransform(slideInVertically { -it }, slideOutVertically { it }) },
                    popTransitionSpec = { ContentTransform(slideInVertically { it }, slideOutVertically { -it }) },
                ) {
                    SplashScreen(onVideoFinish = {
                        if (onboardingState is OnboardingViewModel.OnboardingUiState.Uncompleted) {
                            backStack += NavigationKeys.Onboarding
                            return@SplashScreen
                        }
                        isSplashDone = true
                    })
                }

                is NavigationKeys.Login -> entry(key) {
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

                is NavigationKeys.LoginWithEmailAndPassword -> entry(key) {
                    val (isRegister, isMagicLink) = key
                    LoginWithEmailScreen(
                        isRegister = isRegister,
                        isMagicLink = isMagicLink,
                        onLoginSuccess = {
                            Snapshot.withMutableSnapshot {
                                backStack += NavigationKeys.Home
                                backStack -= key
                            }
                        },
                        loginViewModel = koinActivityViewModel(),
                    )
                }

                is NavigationKeys.Onboarding -> entry(key) {
                    OnboardingScreen(onFinish = {
                        isSplashDone = true
                        backStack -= NavigationKeys.Onboarding
                    }, viewModel = koinActivityViewModel())
                }

                is NavigationKeys.Home -> entry(key) {
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

                is NavigationKeys.AddCamera -> entry(key) {
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

                is NavigationKeys.BrandDetail -> route(key) { (id) ->
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

                is NavigationKeys.Garage -> route(key) { (query, favorites) ->
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

                is NavigationKeys.CarDetail -> route(key) { (id) ->
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

                is NavigationKeys.CarEdit -> route(key) { edit ->
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

                is NavigationKeys.Profile -> entry(key) {
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

                is NavigationKeys.Exchanges -> route(key) { (query) ->
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

                is NavigationKeys.ExchangeCarDetail -> route(key) { (id) ->
                    ExchangeCarDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        carId = id,
                        onExchangeCarClick = { backStack += NavigationKeys.ExchangeCarSelection },
                    )
                }

                is NavigationKeys.ExchangeCarSelection -> entry(key) {
                    ExchangeCarSelectionScreen(
                        onCarClick = { backStack += NavigationKeys.ExchangeCarOffer(it.id) },
                    )
                }

                is NavigationKeys.ExchangeCarOffer -> route(key) { (id) ->
                    ExchangeCarOfferScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        carId = id,
                        onExchangeTemporalClick = { backStack += NavigationKeys.ExchangeConfirmation(it) },
                    )
                }

                is NavigationKeys.ExchangeConfirmation -> route(key) { (id) ->
                    ExchangeConfirmCarScreen(id)
                }
            }
        }
    }
}

private val navigationLogger = Logger.withTag("WheelVault Nav3")

/** Extension function to convert a [CarItem] to a [NavigationKeys.CarEdit] */
private fun CarItem.toCarEdit(): NavigationKeys.CarEdit = this.toBuilder().toCarEdit()

/** Extension function to convert a [CarItem.Builder] to a [NavigationKeys.CarEdit] */
private fun CarItem.Builder.toCarEdit(): NavigationKeys.CarEdit {
    val partial = this
    return NavigationKeys.CarEdit(
        model = partial.model,
        brand = partial.brand,
        year = partial.year,
        quantity = partial.quantity,
        manufacturer = partial.manufacturer,
        isFavorite = partial.isFavorite,
        // this convert is unsafe, but it's ok when links is used
        images = partial.images.map { it.toString() }.toSet(),
        description = partial.description,
        category = partial.category,
        id = partial.id,
    )
}

/** Extension function to convert a [NavigationKeys.CarEdit] to [CarItem.Builder] */
private fun NavigationKeys.CarEdit.toCarPartial(): CarItem.Builder {
    val partial = this
    return CarItem.Builder(
        model = partial.model,
        brand = partial.brand,
        year = partial.year,
        quantity = partial.quantity,
        manufacturer = partial.manufacturer,
        isFavorite = partial.isFavorite,
        images = partial.images,
        description = partial.description,
        category = partial.category,
        id = partial.id,
    )
}

/**
 * Add entry to navigation, this not pass the key to the content.
 *
 * @param key the key for this NavEntry and the content.
 * @param T the type of the key for this NavEntry
 * @param transitionSpec the transition spec for this entry. See [NavDisplay.transitionSpec].
 * @param popTransitionSpec the transition spec when popping this entry from backstack.
 * See [NavDisplay.popTransitionSpec].
 * @param predictivePopTransitionSpec the transition spec when popping this entry from backstack using the predictive back gesture.
 * See [NavDisplay.predictivePopTransitionSpec].
 * @param metadata provides information to the display
 * @param content content for this entry to be displayed when this entry is active with [AnimatedContentScope] of [LocalNavAnimatedContentScope].
 */
private inline fun <T : NavKey> entry(
    key: T,
    noinline transitionSpec: (AnimatedContentTransitionScope<*>.() -> ContentTransform?)? = null,
    noinline popTransitionSpec: (AnimatedContentTransitionScope<*>.() -> ContentTransform?)? = null,
    metadata: Map<String, Any> = emptyMap(),
    noinline predictivePopTransitionSpec: (
        AnimatedContentTransitionScope<Scene<*>>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform?
    )? = null,
    crossinline content: @Composable AnimatedContentScope.() -> Unit,
): NavEntry<NavKey> = NavEntry(
    key = key,
    metadata = buildMap {
        putAll(metadata)
        transitionSpec?.let { putAll(NavDisplay.transitionSpec(transitionSpec)) }
        popTransitionSpec?.let { putAll(NavDisplay.popTransitionSpec(popTransitionSpec)) }
        predictivePopTransitionSpec?.let { putAll(NavDisplay.predictivePopTransitionSpec(predictivePopTransitionSpec)) }
    },
    content = { _ ->
        with(LocalNavAnimatedContentScope.current) {
            content()
        }
    },
)

/**
 * Add a route with data to navigation, this pass the key to the content.
 *
 * @param key the key for this NavEntry and the content.
 * @param T the type of the key for this NavEntry
 * @param transitionSpec the transition spec for this entry. See [NavDisplay.transitionSpec].
 * @param popTransitionSpec the transition spec when popping this entry from backstack.
 * See [NavDisplay.popTransitionSpec].
 * @param predictivePopTransitionSpec the transition spec when popping this entry from backstack using the predictive back gesture.
 * See [NavDisplay.predictivePopTransitionSpec].
 * @param metadata provides information to the display
 * @param content content for this entry to be displayed when this entry is active with [AnimatedContentScope] of [LocalNavAnimatedContentScope].
 */
private inline fun <T : NavKey> route(
    key: T,
    noinline transitionSpec: (AnimatedContentTransitionScope<*>.() -> ContentTransform?)? = null,
    noinline popTransitionSpec: (AnimatedContentTransitionScope<*>.() -> ContentTransform?)? = null,
    noinline predictivePopTransitionSpec: (
        AnimatedContentTransitionScope<Scene<*>>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform?
    )? = null,
    metadata: Map<String, Any> = emptyMap(),
    crossinline content: @Composable AnimatedContentScope.(T) -> Unit,
): NavEntry<NavKey> = NavEntry(
    key = key,
    metadata = buildMap {
        putAll(metadata)
        transitionSpec?.let { putAll(NavDisplay.transitionSpec(transitionSpec)) }
        popTransitionSpec?.let { putAll(NavDisplay.popTransitionSpec(popTransitionSpec)) }
        predictivePopTransitionSpec?.let {
            putAll(NavDisplay.predictivePopTransitionSpec(predictivePopTransitionSpec))
        }
    },
    content = { _ ->
        with(LocalNavAnimatedContentScope.current) {
            content(key)
        }
    },
)

/** Remove all element is not equal to [element] or add [element] if not exist */
private fun <T : NavKey> MutableList<T>.removeAllOrAdd(element: T) {
    Snapshot.withMutableSnapshot {
        val elementIndex = lastIndexOf(element)
        if (elementIndex != -1) {
            removeAll { it != element }
        } else {
            add(element)
        }
    }
}
