package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.CarItemCard
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun GarageContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    uiState: GarageViewModel.GarageUiState,
    topBarState: GarageTopBarState,
    searchQuery: String,
    manufacturerList: List<String>,
    callbacks: GarageCallbacks,
    modifier: Modifier = Modifier,
) {
    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GarageTopBar(
                topBarState,
                searchQuery = searchQuery,
                onSearchQueryChange = callbacks.onSearchQueryChange,
                onStateChange = callbacks.onUiStateChange,
                onSearch = callbacks.onSearchClick,
                topBar = callbacks.filterBar,
                manufacturerList = manufacturerList,
                headersCallbacks = callbacks.headersCallbacks,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = callbacks.onAddClick, containerColor = Color(0xFFE42E31)) {
                Icon(Icons.Filled.Add, stringResource(R.string.add), tint = Color.Black)
            }
        },
    ) { paddingValues ->
        with(sharedTransitionScope) {
            when (uiState) {
                is GarageViewModel.GarageUiState.Success ->
                    PullToRefreshBox(
                        isRefreshing,
                        onRefresh = {
                            isRefreshing = true
                            callbacks.onRefresh()
                            isRefreshing = false
                        },
                        Modifier.fillMaxSize().padding(paddingValues),
                    ) {
                        val carResults = uiState.cars
                        LazyColumn(Modifier.fillMaxSize().padding(start = 15.dp, end = 15.dp)) {
                            item {
                                Text(
                                    stringResource(R.string.garage),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 15.dp, start = 15.dp, bottom = 5.dp),
                                )
                            }
                            items(carResults, key = { it.id }) { item ->
                                CarItemCard(
                                    carItem = item,
                                    onClick = { callbacks.onCarClick(item) },
                                    onFavoriteToggle = { callbacks.onToggleFavorite(item, it) },
                                    modifier = Modifier
                                        .padding(3.dp)
                                        .sharedBounds(
                                            rememberSharedContentState("car-${item.id}"),
                                            animatedVisibilityScope,
                                        ),
                                )
                            }
                        }
                    }

                GarageViewModel.GarageUiState.Error, is GarageViewModel.GarageUiState.Empty -> Column(
                    Modifier.padding(paddingValues).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painterResource(
                            if (uiState is GarageViewModel.GarageUiState.Error) {
                                R.drawable.error
                            } else {
                                R.drawable.no_data
                            },
                        ),
                        null,
                        Modifier.padding(16.dp).fillMaxWidth(0.8f),
                    )
                    if (uiState is GarageViewModel.GarageUiState.Error) {
                        Text(
                            stringResource(R.string.error_loading_of, stringResource(R.string.cars)),
                            color = MaterialTheme.colorScheme.error,
                        )
                    } else {
                        Text(stringResource(R.string.cars_not_found))
                    }
                }

                GarageViewModel.GarageUiState.Loading -> LoadingIndicator(Modifier.padding(paddingValues).fillMaxSize())
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun GaragePreview() {
    WheelVaultTheme {
        SharedTransitionLayout {
            AnimatedVisibility(true) {
                GarageContent(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    uiState = GarageViewModel.GarageUiState.Success(
                        List(10) {
                            CarItem(
                                model = "Ford Mustang GTD",
                                year = 2025,
                                manufacturer = "HotWheels",
                                quantity = 2,
                                imageUrl =
                                "https://tse1.mm.bing.net/th/id/OIP.zfsbW7lEIwYgeUt7Fd1knwHaHg?rs=1&pid=ImgDetMain&o=7&rm=3",
                                isFavorite = true,
                            )
                        },
                    ),
                    topBarState = GarageTopBarState.DEFAULT,
                    searchQuery = "",
                    manufacturerList = BrandViewModel.manufacturerList,
                    callbacks = GarageCallbacks(
                        onHomeClick = {},
                        onSearchQueryChange = {},
                        onAddClick = {},
                        onCarClick = {},
                        onRefresh = {},
                        onUiStateChange = {},
                        onSearchClick = {},
                        headersCallbacks = HeaderCallbacks(
                            onProfileClick = {},
                            onGarageClick = {},
                            onFavoritesClick = {},
                            onStatisticsClick = {},
                        ),
                        onToggleFavorite = { _, _ -> },
                        onFilterByBrand = {},
                        onFilterByFavorite = {},
                        onSortByRecent = {},
                        onSortByLast = {},
                    ),
                )
            }
        }
    }
}
