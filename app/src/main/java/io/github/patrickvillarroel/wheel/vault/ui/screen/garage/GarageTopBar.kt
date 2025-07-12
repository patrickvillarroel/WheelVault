package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

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
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuHeader
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun GarageTopBar(
    uiState: GarageUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onStateChange: (GarageUiState) -> Unit,
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onStaticsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MenuHeader(
        onProfileClick = onProfileClick,
        onGarageClick = { /* TODO the users is already here lol */ },
        onFavoritesClick = onFavoriteClick,
        onStatisticsClick = onStaticsClick,
        modifier = modifier,
    ) {
        AnimatedContent(
            modifier = Modifier.padding(top = 60.dp, start = 2.dp, end = 2.dp),
            targetState = uiState,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(150))
            },
            label = "Search Bar Transition",
        ) { state ->
            when (state) {
                GarageUiState.DEFAULT -> DefaultFilterBar(
                    onSearchClick = { onStateChange(GarageUiState.SEARCH) },
                    onHomeClick = onHomeClick,
                )

                GarageUiState.SEARCH -> SearchBarInput(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onClose = {
                        onSearchQueryChange("")
                        onStateChange(GarageUiState.DEFAULT)
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun TopBarPreview() {
    var uiState by remember { mutableStateOf(GarageUiState.DEFAULT) }
    var searchQuery by remember { mutableStateOf("") }

    WheelVaultTheme {
        GarageTopBar(
            uiState,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onStateChange = { uiState = it },
            onProfileClick = {},
            onHomeClick = {},
            onFavoriteClick = {},
            onStaticsClick = {},
        )
    }
}
