package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import kotlin.uuid.Uuid

class HomeCallbacks(
    val onAddClick: () -> Unit,
    val onSearchClick: () -> Unit,
    val onBrandClick: (Uuid) -> Unit,
    val onNewsClick: (VideoNews) -> Unit,
    val onCarClick: (Uuid) -> Unit,
    val onRefresh: () -> Unit,
    headersCallbacks: HeaderCallbacks,
) : HeaderCallbacks(headersCallbacks) {
    companion object {
        @JvmField
        val default = HomeCallbacks(
            onAddClick = {},
            onSearchClick = {},
            onBrandClick = {},
            onNewsClick = {},
            onCarClick = {},
            onRefresh = {},
            headersCallbacks = HeaderCallbacks.default,
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
