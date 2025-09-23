package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
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

    ExchangeContent(
        topBarState = topBarState,
        searchQuery = searchQuery,
        uiState = uiState,
        manufacturerList = brandsNames,
        callbacks = GarageCallbacks(
            onHomeClick = callbacks.onHomeClick,
            onSearchQueryChange = {
                searchQuery = it
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
                garageViewModel.fetchAll(true)
            },
            onUiStateChange = { topBarState = it },
            headersCallbacks = HeaderCallbacks.default,
            onFilterByBrand = {
                garageViewModel.filterByManufacturer(it)
            },
            onFilterByFavorite = {
                if (it) garageViewModel.fetchFavorites() else garageViewModel.fetchAll(true)
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
