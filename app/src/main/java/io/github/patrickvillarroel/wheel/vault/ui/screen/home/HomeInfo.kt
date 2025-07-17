package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import java.util.UUID

/**
 * @param brands where the first element is the id and the second is the image
 * @param recentCars where the first element is the id and the second is the image
 */
@Immutable
data class HomeInfo(val brands: List<Pair<UUID, Any>>, val news: List<Any>, val recentCars: List<Pair<UUID, Any>>)

class HomeCallbacks(
    @Stable val homeInfo: HomeInfo,
    val onAddClick: () -> Unit,
    val onSearchClick: () -> Unit,
    val onBrandClick: (UUID) -> Unit,
    val onNewsClick: (Any) -> Unit,
    val onCarClick: (UUID) -> Unit,
    headersCallbacks: HeaderCallbacks,
) : HeaderCallbacks(
    headersCallbacks.onProfileClick,
    headersCallbacks.onGarageClick,
    headersCallbacks.onFavoritesClick,
    headersCallbacks.onStatisticsClick,
) {
    constructor(
        brands: List<Pair<UUID, Any>>,
        news: List<Any>,
        recentCars: List<Pair<UUID, Any>>,
        onAddClick: () -> Unit,
        onSearchClick: () -> Unit,
        onBrandClick: (UUID) -> Unit,
        onNewsClick: (Any) -> Unit,
        onCarClick: (UUID) -> Unit,
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
        brands: List<Pair<UUID, Any>>,
        news: List<Any>,
        recentCars: List<Pair<UUID, Any>>,
        onAddClick: () -> Unit,
        onSearchClick: () -> Unit,
        onBrandClick: (UUID) -> Unit,
        onNewsClick: (Any) -> Unit,
        onCarClick: (UUID) -> Unit,
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
    val onBrandClick: (UUID) -> Unit,
    val onCarClick: (UUID) -> Unit,
    val onProfileClick: () -> Unit,
    val onGarageClick: () -> Unit,
    val onFavoritesClick: () -> Unit,
    val onStatisticsClick: () -> Unit,
) {
    val headerCallbacks = HeaderCallbacks(onProfileClick, onGarageClick, onFavoritesClick, onStatisticsClick)
}
