package io.github.patrickvillarroel.wheel.vault.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent
import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.BuildConfig
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.navigation.NavigationKeys.*
import io.github.patrickvillarroel.wheel.vault.ui.screen.camera.CameraLensScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand.BrandDetailScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarDetailScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit.CarEditScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.confirmation.ExchangeConfirmCarScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.detail.ExchangeCarDetailScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.garage.ExchangeScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.history.ExchangeHistoryScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.notifications.ExchangeNotificationsScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.notifications.TradeProposalDetailScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.offer.ExchangeCarOfferScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.selection.ExchangeCarSelectionScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageCallbacks.Partial
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageScreen
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
@Suppress("SENSELESS_COMPARISON") // BuildConfig have constants, so it's fine, change for build types
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
    val backStack = rememberSerializable(
        serializer = NavBackStackSerializer(elementSerializer = NavigationKeys.serializer()),
    ) {
        NavBackStack(Splash)
    }
    var isSplashDone by rememberSaveable { mutableStateOf(false) }

    // prevent exceptions with empty navigation, this trigger recomposition
    if (backStack.isEmpty()) backStack += Home

    LaunchedEffect(Unit) {
        onboardingViewModel.reloadOnboardingState()
    }

    LaunchedEffect(session, isSplashDone) {
        if (!isSplashDone) return@LaunchedEffect

        when (session) {
            is SessionUiStatus.Authenticated -> {
                if (backStack.lastOrNull() is LoginWithEmailAndPassword ||
                    backStack.lastOrNull() is Login ||
                    backStack.lastOrNull() is Splash ||
                    backStack.lastOrNull() is Onboarding
                ) {
                    Snapshot.withMutableSnapshot {
                        if (!backStack.contains(Home)) backStack += Home
                        backStack.removeAll { it !is Home }
                    }
                }
            }

            SessionUiStatus.NotAuthenticated,
            SessionUiStatus.RefreshFailure,
            -> {
                Snapshot.withMutableSnapshot {
                    onboardingViewModel.reloadOnboardingState()
                    if (!backStack.contains(Login)) backStack += Login
                    backStack.removeAll { it !is Login }
                }
            }

            SessionUiStatus.Initializing -> Unit
        }
    }

    val headerCallbacks = remember {
        HeaderBackCallbacks(
            onBackClick = { backStack.removeLastOrNull() },
            onProfileClick = { backStack += Profile },
            onGarageClick = { backStack += Garage() },
            onFavoritesClick = { backStack += Garage(favorites = true) },
            onStatisticsClick = { backStack += Garage(statistics = true) },
            onExchangesClick = { backStack += Exchanges() },
            onNotificationsClick = { backStack += ExchangeNotifications },
            onHomeClick = { backStack.removeAllOrAdd(Home) },
        )
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
            when (key) {
                is Splash -> route(
                    key,
                    transitionSpec = { ContentTransform(slideInVertically { -it }, slideOutVertically { it }) },
                    popTransitionSpec = { ContentTransform(slideInVertically { it }, slideOutVertically { -it }) },
                ) {
                    SplashScreen(onVideoFinish = {
                        if (onboardingState is OnboardingViewModel.OnboardingUiState.Uncompleted) {
                            backStack += Onboarding
                            return@SplashScreen
                        }
                        isSplashDone = true
                    })
                }

                is Login -> route(key) {
                    LoginScreen(
                        onLoginSuccess = {
                            Snapshot.withMutableSnapshot {
                                backStack += Home
                                backStack -= Login
                            }
                        },
                        onLoginWithEmail = { backStack += LoginWithEmailAndPassword(isMagicLink = true) },
                        onLoginWithEmailAndPasswordClick = {
                            backStack += LoginWithEmailAndPassword(isRegister = false)
                        },
                        onRegisterClick = { backStack += LoginWithEmailAndPassword(isRegister = true) },
                    )
                }

                is LoginWithEmailAndPassword -> route(key) {
                    val (isRegister, isMagicLink) = key
                    LoginWithEmailScreen(
                        isRegister = isRegister,
                        isMagicLink = isMagicLink,
                        onLoginSuccess = {
                            Snapshot.withMutableSnapshot {
                                backStack += Home
                                backStack -= key
                            }
                        },
                    )
                }

                is Onboarding -> route(key) {
                    OnboardingScreen(onFinish = {
                        isSplashDone = true
                        backStack -= Onboarding
                    })
                }

                is Home -> route(key) {
                    HomeScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        onAddClick = { backStack += AddCamera },
                        onSearchClick = { backStack += Garage("") },
                        onBrandClick = { backStack += BrandDetail(it) },
                        onCarClick = { backStack += CarDetail(it) },
                        callbacks = headerCallbacks,
                    )
                }

                is AddCamera -> route(key) {
                    CameraLensScreen(
                        onBack = { backStack.removeLastOrNull() },
                        onAddDetail = { carModel, _ ->
                            Snapshot.withMutableSnapshot {
                                backStack += CarEdit(model = carModel, fromCamera = true)
                                backStack -= AddCamera
                            }
                        },
                        viewModel = koinActivityViewModel(),
                    )
                }

                is BrandDetail -> route(key) {
                    BrandDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        brandId = key.id,
                        headerBackCallbacks = headerCallbacks,
                        onAddClick = { backStack += AddCamera },
                        onCarClick = { backStack += CarDetail(it) },
                    )
                }

                is Garage -> route(key) {
                    val (query, favorites) = key
                    GarageScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        query = query ?: "",
                        favorites = favorites,
                        callbacks = Partial(
                            onHomeClick = { backStack.removeAllOrAdd(Home) },
                            onCarClick = { backStack += CarDetail(it) },
                            onAddClick = { backStack += AddCamera },
                            onProfileClick = { backStack += Profile },
                            onExchangesClick = { backStack += Exchanges() },
                            onTradeHistoryClick = { backStack += ExchangesHistory },
                            onNotificationsClick = { backStack += ExchangeNotifications },
                        ),
                    )
                }

                is CarDetail -> route(key) {
                    CarDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        carId = key.id,
                        onEditClick = { backStack += it.toCarEdit() },
                        headerBackCallbacks = headerCallbacks,
                    )
                }

                is CarEdit -> route(key) {
                    CarEditScreen(
                        partialCarItem = key.toCarPartial(),
                        fromCamera = key.fromCamera,
                        headersBackCallbacks = headerCallbacks,
                        cameraViewModel = koinActivityViewModel(),
                    )
                }

                is Profile -> route(key) { ProfileScreen(backCallbacks = headerCallbacks) }

                is Exchanges -> route(key) {
                    if (!BuildConfig.ENABLE_TRADING) {
                        navigationLogger.e {
                            "Exchanges not available and reached in navigation routes. Query='${key.query}'"
                        }
                    }
                    ExchangeScreen(
                        query = key.query ?: "",
                        callbacks = Partial(
                            onHomeClick = { backStack.removeAllOrAdd(Home) },
                            onCarClick = { backStack += ExchangeCarDetail(it) },
                            onAddClick = { backStack += AddCamera },
                            onProfileClick = { backStack += Profile },
                            onExchangesClick = {},
                            onNotificationsClick = { backStack += ExchangeNotifications },
                            onTradeHistoryClick = { backStack += ExchangesHistory },
                        ),
                    )
                }

                is ExchangeCarDetail -> route(key) {
                    if (!BuildConfig.ENABLE_TRADING) {
                        navigationLogger.e {
                            "Exchanges not available and reached in navigation routes. CarId='${key.id}'"
                        }
                    }
                    ExchangeCarDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        carId = key.id,
                        onExchangeCarClick = { backStack += ExchangeCarSelection },
                        headerBackCallbacks = headerCallbacks,
                    )
                }

                is ExchangeCarSelection -> route(key) {
                    if (!BuildConfig.ENABLE_TRADING) {
                        navigationLogger.e {
                            "Exchanges not available and reached in navigation routes for car selection."
                        }
                    }
                    ExchangeCarSelectionScreen(
                        onCarClick = { backStack += ExchangeCarOffer(it.id) },
                        headerCallbacks = headerCallbacks,
                    )
                }

                is ExchangeCarOffer -> route(key) {
                    if (!BuildConfig.ENABLE_TRADING) {
                        navigationLogger.e {
                            "Exchanges not available and reached in navigation routes. Offers of CarId='${key.id}'"
                        }
                    }
                    ExchangeCarOfferScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        onExchangeTemporalClick = { backStack += ExchangeConfirmation(it) },
                        headerCallbacks = headerCallbacks,
                    )
                }

                is ExchangeConfirmation -> route(key) {
                    if (!BuildConfig.ENABLE_TRADING) {
                        navigationLogger.e {
                            "Exchanges not available and reached in navigation routes. Confirm of CarId='${key.id}'"
                        }
                    }
                    ExchangeConfirmCarScreen(
                        requestCarId = key.id,
                        headerBackCallbacks = headerCallbacks,
                    )
                }

                ExchangeNotifications -> route(key) {
                    if (!BuildConfig.ENABLE_TRADING) {
                        navigationLogger.e {
                            "Exchanges not available and reached in navigation routes. Notifications"
                        }
                    }
                    ExchangeNotificationsScreen(
                        headerCallbacks = headerCallbacks,
                        onTradeClick = { notification ->
                            backStack += TradeProposalDetail(notification.trade.tradeGroupId)
                        },
                    )
                }

                is TradeProposalDetail -> route(key) {
                    if (!BuildConfig.ENABLE_TRADING) {
                        navigationLogger.e {
                            "Exchanges not available and reached in navigation routes. Trade Detail: ${key.tradeGroupId}"
                        }
                    }
                    TradeProposalDetailScreen(
                        tradeGroupId = key.tradeGroupId,
                        headerCallbacks = headerCallbacks,
                        onTradeActionFinish = { backStack.removeLastOrNull() },
                    )
                }

                ExchangesHistory -> route(key) {
                    if (!BuildConfig.ENABLE_TRADING) {
                        navigationLogger.e { "Exchanges not available and reached in navigation routes. History" }
                    }
                    ExchangeHistoryScreen(headerCallbacks = headerCallbacks)
                }
            }
        }
    }
}

private val navigationLogger = Logger.withTag("WheelVault Nav3")

/** Extension function to convert a [CarItem] to a [CarEdit] */
private fun CarItem.toCarEdit() = this.toBuilder().toCarEdit()

/** Extension function to convert a [CarItem.Builder] to a [CarEdit] */
private fun CarItem.Builder.toCarEdit() = CarEdit(
    model = this.model,
    brand = this.brand,
    year = this.year,
    quantity = this.quantity,
    manufacturer = this.manufacturer,
    isFavorite = this.isFavorite,
    // Only navigate with links (strings)
    images = this.images.filterIsInstance<String>().toSet(),
    description = this.description,
    category = this.category,
    id = this.id,
)

/** Extension function to convert a [CarEdit] to [CarItem.Builder] */
private fun CarEdit.toCarPartial() = CarItem.Builder(
    model = this.model,
    brand = this.brand,
    year = this.year,
    quantity = this.quantity,
    manufacturer = this.manufacturer,
    isFavorite = this.isFavorite,
    images = this.images,
    description = this.description,
    category = this.category,
    id = this.id,
)

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
private inline fun <T : NavKey> route(
    key: T,
    noinline transitionSpec: (AnimatedContentTransitionScope<*>.() -> ContentTransform?)? = null,
    noinline popTransitionSpec: (AnimatedContentTransitionScope<*>.() -> ContentTransform?)? = null,
    metadata: Map<String, Any> = emptyMap(),
    noinline predictivePopTransitionSpec: (
        AnimatedContentTransitionScope<Scene<*>>.(swipe: @NavigationEvent.SwipeEdge Int) -> ContentTransform?
    )? = null,
    crossinline content: @Composable AnimatedContentScope.() -> Unit,
) = NavEntry(
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
