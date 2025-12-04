package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.garage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.ExchangeViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageTopBarState
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExchangeScreen(
    query: String,
    callbacks: GarageCallbacks.Partial,
    modifier: Modifier = Modifier,
    garageViewModel: GarageViewModel = koinViewModel(),
    exchangeViewModel: ExchangeViewModel = koinViewModel(),
    carViewModel: CarViewModel = koinViewModel(),
    brandViewModel: BrandViewModel = koinViewModel(),
) {
    val brandsNames by brandViewModel.brandsNames.collectAsStateWithLifecycle()
    val uiState by exchangeViewModel.exchangeState.collectAsStateWithLifecycle()
    var searchQuery by rememberSaveable { mutableStateOf(query.trim()) }
    var topBarState by rememberSaveable { mutableStateOf(GarageTopBarState.DEFAULT) }

    LaunchedEffect(Unit) {
        exchangeViewModel.loadInitialData(forceRefresh = false)
        garageViewModel.fetchAll()
        brandViewModel.fetchNames()
    }

    ExchangeContent(
        uiState = uiState,
        topBarState = topBarState,
        searchQuery = searchQuery,
        manufacturerList = brandsNames,
        onLoadMore = { exchangeViewModel.loadMoreCars() },
        onTradeHistoryClick = { callbacks.onTradeHistoryClick() },
        callbacks = GarageCallbacks(
            onHomeClick = callbacks.onHomeClick,
            onSearchQueryChange = { newSearchQuery ->
                @Suppress("ASSIGNED_VALUE_IS_NEVER_READ")
                searchQuery = newSearchQuery
            },
            onSearchClick = {
                garageViewModel.search(searchQuery)
            },
            onAddClick = callbacks.onAddClick,
            onCarClick = { callbacks.onCarClick(it.id) },
            onToggleFavorite = { car, isFavorite ->
                carViewModel.save(car.copy(isFavorite = isFavorite))
            },
            onRefresh = {
                exchangeViewModel.loadInitialData(forceRefresh = true)
                garageViewModel.fetchAll(true)
                brandViewModel.fetchNames(true)
            },
            onUiStateChange = { newTopBarState ->
                @Suppress("ASSIGNED_VALUE_IS_NEVER_READ")
                topBarState = newTopBarState
            },
            headersCallbacks = HeaderCallbacks(
                onProfileClick = callbacks.onProfileClick,
                onGarageClick = {},
                onFavoritesClick = {},
                onStatisticsClick = {},
                onExchangesClick = callbacks.onExchangesClick,
                onNotificationsClick = callbacks.onNotificationsClick,
                onHomeClick = callbacks.onHomeClick,
            ),
            onFilterByBrand = { manufacturerName ->
                garageViewModel.filterByManufacturer(manufacturerName)
            },
            onFilterByFavorite = { isFavoriteFilterEnabled ->
                if (isFavoriteFilterEnabled) garageViewModel.fetchFavorites() else garageViewModel.fetchAll(true)
            },
            onSortByRecent = {
                garageViewModel.fetchAll(force = true, orderAsc = false)
            },
            onSortByLast = {
                garageViewModel.fetchAll(force = true, orderAsc = true)
            },
        ),
        modifier = modifier,
    )
}
