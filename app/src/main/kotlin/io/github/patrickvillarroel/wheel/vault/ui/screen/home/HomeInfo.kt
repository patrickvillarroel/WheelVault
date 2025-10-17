package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import kotlin.uuid.Uuid

/**
 * @param brands where the first element is the id and the second is the image
 * @param recentCars where the first element is the id and the second is the image
 */
@Immutable
data class HomeInfo(
    @Stable val brands: List<Pair<Uuid, Any>>,
    @Stable val news: List<VideoNews>,
    @Stable val recentCars: List<Pair<Uuid, Any>>,
)

class HomeCallbacks(
    @Stable val homeInfo: HomeInfo,
    val onAddClick: () -> Unit,
    val onSearchClick: () -> Unit,
    val onBrandClick: (Uuid) -> Unit,
    val onNewsClick: (VideoNews) -> Unit,
    val onCarClick: (Uuid) -> Unit,
    val onRefresh: () -> Unit,
    headersCallbacks: HeaderCallbacks,
) : HeaderCallbacks(headersCallbacks) {
    constructor(
        brands: List<Pair<Uuid, Any>>,
        news: List<VideoNews>,
        recentCars: List<Pair<Uuid, Any>>,
        onAddClick: () -> Unit,
        onSearchClick: () -> Unit,
        onBrandClick: (Uuid) -> Unit,
        onNewsClick: (VideoNews) -> Unit,
        onCarClick: (Uuid) -> Unit,
        onRefresh: () -> Unit,
        headerCallbacks: HeaderCallbacks,
    ) : this(
        homeInfo = HomeInfo(brands, news, recentCars),
        onAddClick,
        onSearchClick,
        onBrandClick,
        onNewsClick,
        onCarClick,
        onRefresh,
        headerCallbacks,
    )

    companion object {
        fun default(brands: List<Pair<Uuid, Any>>, news: List<VideoNews>, recentCars: List<Pair<Uuid, Any>>) =
            HomeCallbacks(
                brands = brands,
                news = news,
                recentCars = recentCars,
                onAddClick = {},
                onSearchClick = {},
                onBrandClick = {},
                onNewsClick = {},
                onCarClick = {},
                onRefresh = {},
                headerCallbacks = default,
            )
    }
}

class HomeNavCallbacks(
    val onAddClick: () -> Unit,
    val onSearchClick: () -> Unit,
    val onBrandClick: (Uuid) -> Unit,
    val onCarClick: (Uuid) -> Unit,
    val onProfileClick: () -> Unit,
    val onGarageClick: () -> Unit,
    val onFavoritesClick: () -> Unit,
    val onStatisticsClick: () -> Unit,
    val onExchangesClick: () -> Unit,
) {
    @JvmField
    val headerCallbacks =
        HeaderCallbacks(onProfileClick, onGarageClick, onFavoritesClick, onStatisticsClick, onExchangesClick)
}
