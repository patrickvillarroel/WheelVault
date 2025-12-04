package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GarageScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    query: String,
    favorites: Boolean,
    callbacks: GarageCallbacks.Partial,
    modifier: Modifier = Modifier,
    viewModel: GarageViewModel = koinViewModel(),
    carViewModel: CarViewModel = koinViewModel(),
    brandViewModel: BrandViewModel = koinViewModel(),
) {
    val brandsNames by brandViewModel.brandsNames.collectAsStateWithLifecycle()
    val carsPaged = viewModel.carsPaged.collectAsLazyPagingItems()
    var uiState by rememberSaveable { mutableStateOf(GarageTopBarState.DEFAULT) }
    var searchQuery by rememberSaveable { mutableStateOf(query.trim()) }

    LaunchedEffect(Unit) {
        brandViewModel.fetchNames()
    }

    // Update filters when query or favorites change - pagination will automatically refresh
    LaunchedEffect(query, favorites) {
        viewModel.setSearchQuery(query.takeIf { it.isNotBlank() })
        viewModel.setFavoriteFilter(favorites)
    }

    GarageContent(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        carsPaged = carsPaged,
        topBarState = uiState,
        searchQuery = searchQuery,
        manufacturerList = brandsNames,
        callbacks = GarageCallbacks(
            onHomeClick = callbacks.onHomeClick,
            onSearchQueryChange = { query ->
                @Suppress("ASSIGNED_VALUE_IS_NEVER_READ")
                searchQuery = query
            },
            onSearchClick = {
                viewModel.setSearchQuery(searchQuery)
            },
            onAddClick = callbacks.onAddClick,
            onCarClick = { callbacks.onCarClick(it.id) },
            onToggleFavorite = { car, isFavorite ->
                carViewModel.save(car.copy(isFavorite = isFavorite))
            },
            onRefresh = {
                carsPaged.refresh()
                brandViewModel.fetchNames(true)
            },
            onUiStateChange = { newUiState ->
                @Suppress("ASSIGNED_VALUE_IS_NEVER_READ")
                uiState = newUiState
            },
            headersCallbacks = HeaderCallbacks(
                onProfileClick = callbacks.onProfileClick,
                onGarageClick = {
                    viewModel.clearFilters()
                },
                onFavoritesClick = {
                    viewModel.setFavoriteFilter(true)
                },
                onStatisticsClick = {},
                onExchangesClick = callbacks.onExchangesClick,
                onNotificationsClick = { callbacks.onNotificationsClick() },
                onHomeClick = callbacks.onHomeClick,
            ),
            onFilterByBrand = { manufacturerName ->
                viewModel.setManufacturerFilter(manufacturerName)
            },
            onFilterByFavorite = { isFavorite ->
                viewModel.setFavoriteFilter(isFavorite)
            },
            onSortByRecent = {
                viewModel.setSortOrder(orderAsc = false)
            },
            onSortByLast = {
                viewModel.setSortOrder(orderAsc = true)
            },
        ),
        modifier = modifier,
    )
}
