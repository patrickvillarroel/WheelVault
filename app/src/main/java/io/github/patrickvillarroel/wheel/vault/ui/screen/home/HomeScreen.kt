package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.patrickvillarroel.wheel.vault.R

@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onSearchClick: () -> Unit,
    onBrandClick: (Int) -> Unit,
    onCarClick: (Int) -> Unit,
    onGarageClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO use VM or something
    val brands = listOf(1 to R.drawable.hot_wheels_logo_black)
    val news = listOf(R.drawable.thumbnail_example)
    val recentCars = listOf(R.drawable.batman_car)

    HomeContent(
        brands = brands,
        news = news,
        recentCars = recentCars,
        onAddClick = onAddClick,
        onSearchClick = onSearchClick,
        onBrandClick = onBrandClick,
        onGarageClick = onGarageClick,
        onFavoritesClick = onFavoritesClick,
        onStatisticsClick = onStatisticsClick,
        onProfileClick = onProfileClick,
        onNewsClick = {},
        onCardClick = onCarClick,
        modifier = modifier,
    )
}
