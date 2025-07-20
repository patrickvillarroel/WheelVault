package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuHeader
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun GarageTopBar(
    uiState: GarageTopBarState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onStateChange: (GarageTopBarState) -> Unit,
    onSearch: () -> Unit,
    manufacturerList: List<String>,
    topBar: GarageCallbacks.FilterBar,
    headersCallbacks: HeaderCallbacks,
    modifier: Modifier = Modifier,
) {
    MenuHeader(
        headerCallbacks = headersCallbacks,
        modifier = modifier,
    ) {
        AnimatedContent(
            modifier = Modifier.padding(top = 40.dp),
            targetState = uiState,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(150))
            },
            label = "Search Bar Transition",
        ) { state ->
            when (state) {
                GarageTopBarState.DEFAULT -> DefaultFilterBar(
                    manufacturerList = manufacturerList,
                    onSearchClick = { onStateChange(GarageTopBarState.SEARCH) },
                    callbacks = topBar,
                )

                GarageTopBarState.SEARCH -> {
                    BackHandler { onStateChange(GarageTopBarState.DEFAULT) }
                    SearchBarInput(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange,
                        onSearch = onSearch,
                        onClose = {
                            onSearchQueryChange("")
                            onStateChange(GarageTopBarState.DEFAULT)
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopBarPreview() {
    var uiState by remember { mutableStateOf(GarageTopBarState.DEFAULT) }
    var searchQuery by remember { mutableStateOf("") }

    WheelVaultTheme {
        GarageTopBar(
            uiState,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onStateChange = { uiState = it },
            onSearch = {},
            topBar = GarageCallbacks.FilterBar(
                onHomeClick = {},
                onFilterByBrand = {},
                onFilterByFavorite = {},
                onSortByRecent = {},
                onSortByLast = {},
            ),
            manufacturerList = BrandViewModel.manufacturerList,
            headersCallbacks = HeaderCallbacks(
                onProfileClick = {},
                onGarageClick = {},
                onFavoritesClick = {},
                onStatisticsClick = {},
            ),
        )
    }
}
