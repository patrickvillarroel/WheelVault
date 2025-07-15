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

/**
 * TODO change all callbacks to a data class
 * @param brands is [(id, drawable)] id must be unique
 * @param news is [link/painter] must be unique
 * @param recentCars is [id, link/drawable] id must be unique
 */
@Composable
fun HomeContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    brands: List<Pair<Int, Int>>,
    news: List<Any>,
    recentCars: List<Pair<Int, Int>>,
    onAddClick: () -> Unit,
    onSearchClick: () -> Unit,
    onBrandClick: (Int) -> Unit,
    onGarageClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNewsClick: (Any) -> Unit,
    onCarClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            HomeCarHeader(
                onProfileClick = onProfileClick,
                onGarageClick = onGarageClick,
                onFavoritesClick = onFavoritesClick,
                onStatisticsClick = onStatisticsClick,
            )
        },
        floatingActionButton = { HomeFloatingButton(onAddClick = onAddClick, onSearchClick = onSearchClick) },
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
                                onClick = { onBrandClick(id) },
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
                                onClick = { onCarClick(id) },
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
                            VideoCardPreview(it, onPlayClick = { onNewsClick(it) })
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
                    brands = List(10) { it to R.drawable.hot_wheels_logo_black },
                    news = listOf(R.drawable.thumbnail_example),
                    recentCars = listOf(1 to R.drawable.batman_car),
                    onAddClick = {},
                    onSearchClick = {},
                    onBrandClick = {},
                    onGarageClick = {},
                    onFavoritesClick = {},
                    onStatisticsClick = {},
                    onProfileClick = {},
                    onNewsClick = {},
                    onCarClick = {},
                )
            }
        }
    }
}
