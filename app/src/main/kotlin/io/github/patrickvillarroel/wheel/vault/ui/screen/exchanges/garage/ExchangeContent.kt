package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.garage

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.CarNameCard
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.ExchangeViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageTopBar
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageTopBarState
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun ExchangeContent(
    uiState: ExchangeViewModel.ExchangeUiState,
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
    ) { paddingValues ->
        AnimatedContent(uiState) { state ->
            when (state) {
                is ExchangeViewModel.ExchangeUiState.Success -> PullToRefreshBox(
                    isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        callbacks.onRefresh()
                        isRefreshing = false
                    },
                    Modifier.fillMaxSize().padding(paddingValues),
                ) {
                    val carResults = state.cars
                    Column(Modifier.fillMaxSize().padding(start = 15.dp, end = 15.dp)) {
                        Text(
                            text = stringResource(R.string.exchange),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(top = 15.dp, start = 15.dp, bottom = 10.dp)
                                .drawBehind {
                                    val underlineHeight = 1.dp.toPx()
                                    val y = size.height
                                    drawLine(
                                        color = Color(0xFFE42E31),
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = underlineHeight,
                                    )
                                },
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(carResults, key = { it.id }) { car ->
                                CarNameCard(
                                    image = car.imageUrl,
                                    name = car.model,
                                    onClick = { callbacks.onCarClick(car) },
                                )
                            }
                        }
                    }
                }

                ExchangeViewModel.ExchangeUiState.Loading -> LoadingIndicator(
                    Modifier.padding(paddingValues).fillMaxSize(),
                )

                ExchangeViewModel.ExchangeUiState.Error -> Column(
                    Modifier.padding(paddingValues).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painterResource(R.drawable.error),
                        null,
                        Modifier.padding(16.dp).fillMaxWidth(0.8f),
                    )
                    Text(
                        stringResource(R.string.error_loading_of, stringResource(R.string.cars)),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ExchangePreview() {
    WheelVaultTheme {
        ExchangeContent(
            uiState = ExchangeViewModel.ExchangeUiState.Success(
                List(50) {
                    CarItem(
                        model = "Ford Mustang GTD",
                        year = 2025,
                        manufacturer = "HotWheels",
                        quantity = 2,
                        imageUrl = painterResource(R.drawable.batman_car),
                        isFavorite = true,
                    )
                },
            ),
            topBarState = GarageTopBarState.DEFAULT,
            searchQuery = "",
            manufacturerList = BrandViewModel.manufacturerList,
            callbacks = GarageCallbacks.default,
        )
    }
}
