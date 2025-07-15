package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.compose.runtime.Immutable
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks

@Immutable
data class HomeInfo(val brands: List<Pair<Int, Int>>, val news: List<Any>, val recentCars: List<Pair<Int, Int>>)

class HomeCallbacks(
    val homeInfo: HomeInfo,
    val onAddClick: () -> Unit,
    val onSearchClick: () -> Unit,
    val onBrandClick: (Int) -> Unit,
    val onNewsClick: (Any) -> Unit,
    val onCarClick: (Int) -> Unit,
    headersCallbacks: HeaderCallbacks,
) : HeaderCallbacks(
    headersCallbacks.onProfileClick,
    headersCallbacks.onGarageClick,
    headersCallbacks.onFavoritesClick,
    headersCallbacks.onStatisticsClick,
) {
    constructor(
        brands: List<Pair<Int, Int>>,
        news: List<Any>,
        recentCars: List<Pair<Int, Int>>,
        onAddClick: () -> Unit,
        onSearchClick: () -> Unit,
        onBrandClick: (Int) -> Unit,
        onNewsClick: (Any) -> Unit,
        onCarClick: (Int) -> Unit,
        onProfileClick: () -> Unit,
        onGarageClick: () -> Unit,
        onFavoritesClick: () -> Unit,
        onStatisticsClick: () -> Unit,
    ) : this(
        homeInfo = HomeInfo(brands, news, recentCars),
        onAddClick,
        onSearchClick,
        onBrandClick,
        onNewsClick,
        onCarClick,
        HeaderCallbacks(onProfileClick, onGarageClick, onFavoritesClick, onStatisticsClick),
    )

    constructor(
        brands: List<Pair<Int, Int>>,
        news: List<Any>,
        recentCars: List<Pair<Int, Int>>,
        onAddClick: () -> Unit,
        onSearchClick: () -> Unit,
        onBrandClick: (Int) -> Unit,
        onNewsClick: (Any) -> Unit,
        onCarClick: (Int) -> Unit,
        headerCallbacks: HeaderCallbacks,
    ) : this(
        homeInfo = HomeInfo(brands, news, recentCars),
        onAddClick,
        onSearchClick,
        onBrandClick,
        onNewsClick,
        onCarClick,
        headerCallbacks,
    )
}

data class HomeNavCallbacks(
    val onAddClick: () -> Unit,
    val onSearchClick: () -> Unit,
    val onBrandClick: (Int) -> Unit,
    val onCarClick: (Int) -> Unit,
    val onProfileClick: () -> Unit,
    val onGarageClick: () -> Unit,
    val onFavoritesClick: () -> Unit,
    val onStatisticsClick: () -> Unit,
) {
    val headerCallbacks = HeaderCallbacks(onProfileClick, onGarageClick, onFavoritesClick, onStatisticsClick)
}
