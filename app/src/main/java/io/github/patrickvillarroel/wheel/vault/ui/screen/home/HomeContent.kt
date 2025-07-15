package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BrandCard
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.CarCard
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.RaceDivider
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.VideoCardPreview
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun HomeContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    info: HomeCallbacks,
    modifier: Modifier = Modifier,
) {
    val (brands, news, recentCars) = remember(info.homeInfo) { info.homeInfo }

    Scaffold(
        modifier = modifier,
        topBar = { HomeCarHeader(info) },
        floatingActionButton = { HomeFloatingButton(onAddClick = info.onAddClick, onSearchClick = info.onSearchClick) },
    ) { paddingValues ->
        with(sharedTransitionScope) {
            LazyColumn(Modifier.fillMaxSize().padding(paddingValues)) {
                item { Spacer(modifier = Modifier.height(16.dp)) }

                // Sección de marcas
                item {
                    Text(
                        text = stringResource(R.string.brands),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(brands, key = { (id, _) -> id }) { (id, image) ->
                            BrandCard(
                                logo = image,
                                onClick = { info.onBrandClick(id) },
                                modifier = Modifier.sharedBounds(
                                    rememberSharedContentState("brand-$id"),
                                    animatedVisibilityScope,
                                ),
                            )
                        }
                    }
                }

                item { RaceDivider() }

                // Sección de recientes
                item {
                    Text(
                        text = stringResource(R.string.recently_added),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(recentCars, key = { (id, _) -> id }) { (id, image) ->
                            CarCard(
                                image = image,
                                onClick = { info.onCarClick(id) },
                                modifier = Modifier.sharedBounds(
                                    rememberSharedContentState("car-$id"),
                                    animatedVisibilityScope,
                                ),
                            )
                        }
                    }
                }

                item { RaceDivider() }

                item {
                    Text(
                        text = stringResource(R.string.information_interest),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(news, key = { it }) {
                            VideoCardPreview(it, onPlayClick = { info.onNewsClick(it) })
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeContentPreview() {
    SharedTransitionLayout {
        AnimatedVisibility(true) {
            WheelVaultTheme {
                HomeContent(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    info = HomeCallbacks(
                        brands = List(10) { it to R.drawable.hot_wheels_logo_black },
                        news = listOf(R.drawable.thumbnail_example),
                        recentCars = listOf(1 to R.drawable.batman_car),
                        onAddClick = {},
                        onSearchClick = {},
                        onBrandClick = {},
                        onNewsClick = {},
                        onCarClick = {},
                        onProfileClick = {},
                        onGarageClick = {},
                        onFavoritesClick = {},
                        onStatisticsClick = {},
                    ),
                )
            }
        }
    }
}
