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
) {
    val carState by viewModel.garageState.collectAsStateWithLifecycle()
    var uiState by rememberSaveable { mutableStateOf(GarageTopBarState.DEFAULT) }
    var searchQuery by rememberSaveable { mutableStateOf(query.trim()) }

    LaunchedEffect(query, favorites) {
        when {
            favorites && query.isBlank() -> viewModel.fetchFavorites()
            favorites && query.isNotBlank() -> viewModel.search(query, true)
            !favorites && query.isBlank() -> viewModel.fetchAll(true)
            !favorites && query.isNotBlank() -> viewModel.search(query)
        }
    }

    GarageContent(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        uiState = carState,
        topBarState = uiState,
        searchQuery = searchQuery,
        callbacks = GarageCallbacks(
            onHomeClick = callbacks.onHomeClick,
            onSearchQueryChange = {
                searchQuery = it
            },
            onSearchClick = {
                viewModel.search(searchQuery)
            },
            onAddClick = callbacks.onAddClick,
            onCarClick = { callbacks.onCarClick(it.id) },
            onToggleFavorite = { car, isFavorite ->
                carViewModel.save(car.copy(isFavorite = isFavorite))
            },
            onRefresh = {
                viewModel.fetchAll(true)
            },
            onUiStateChange = { uiState = it },
            headersCallbacks = HeaderCallbacks(
                onProfileClick = callbacks.onProfileClick,
                onGarageClick = {
                    viewModel.fetchAll(true)
                },
                onFavoritesClick = {
                    viewModel.fetchFavorites()
                },
                onStatisticsClick = {},
            ),
            onFilterByBrand = {
                viewModel.filterByBrand(it)
            },
            onFilterByFavorite = {
                if (it) viewModel.fetchFavorites() else viewModel.fetchAll(true)
            },
            onSortByRecent = {
                viewModel.fetchAll(force = true, orderAsc = false)
            },
            onSortByLast = {
                viewModel.fetchAll(force = true, orderAsc = true)
            },
        ),
        modifier = modifier,
    )
}
