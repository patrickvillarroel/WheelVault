package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import kotlin.uuid.Uuid

class GarageCallbacks(
    val onSearchQueryChange: (String) -> Unit,
    val onAddClick: () -> Unit,
    val onCarClick: (CarItem) -> Unit,
    val onToggleFavorite: (CarItem, Boolean) -> Unit,
    val onRefresh: () -> Unit,
    val onUiStateChange: (GarageTopBarState) -> Unit,
    val onSearchClick: () -> Unit,
    val filterBar: FilterBar,
    val headersCallbacks: HeaderCallbacks,
) {
    constructor(
        onHomeClick: () -> Unit,
        onSearchQueryChange: (String) -> Unit,
        onAddClick: () -> Unit,
        onCarClick: (CarItem) -> Unit,
        onToggleFavorite: (CarItem, Boolean) -> Unit,
        onRefresh: () -> Unit,
        onUiStateChange: (GarageTopBarState) -> Unit,
        onSearchClick: () -> Unit,
        headersCallbacks: HeaderCallbacks,
        onFilterByBrand: (String) -> Unit,
        onFilterByFavorite: (Boolean) -> Unit,
        onSortByRecent: () -> Unit,
        onSortByLast: () -> Unit,
    ) : this(
        onSearchQueryChange,
        onAddClick,
        onCarClick,
        onToggleFavorite,
        onRefresh,
        onUiStateChange,
        onSearchClick,
        FilterBar(onHomeClick, onFilterByBrand, onFilterByFavorite, onSortByRecent, onSortByLast),
        headersCallbacks,
    )

    companion object {
        @JvmField
        val default = GarageCallbacks(
            onSearchQueryChange = {},
            onAddClick = {},
            onCarClick = {},
            onToggleFavorite = { _, _ -> },
            onRefresh = {},
            onUiStateChange = {},
            onSearchClick = {},
            filterBar = FilterBar.default,
            headersCallbacks = HeaderCallbacks.default,
        )
    }

    class FilterBar(
        val onHomeClick: () -> Unit,
        val onFilterByBrand: (String) -> Unit,
        val onFilterByFavorite: (Boolean) -> Unit,
        val onSortByRecent: () -> Unit,
        val onSortByLast: () -> Unit,
    ) {
        companion object {
            @JvmField
            val default = FilterBar(
                onHomeClick = {},
                onFilterByBrand = {},
                onFilterByFavorite = {},
                onSortByRecent = {},
                onSortByLast = {},
            )
        }
    }

    class Partial(
        val onHomeClick: () -> Unit,
        val onAddClick: () -> Unit,
        val onCarClick: (Uuid) -> Unit,
        val onProfileClick: () -> Unit,
        val onExchangesClick: () -> Unit,
        val onNotificationsClick: () -> Unit,
        val onTradeHistoryClick: () -> Unit,
    ) {
        companion object {
            @JvmField
            val default = Partial(
                onHomeClick = {},
                onAddClick = {},
                onCarClick = {},
                onProfileClick = {},
                onExchangesClick = {},
                onNotificationsClick = {},
                onTradeHistoryClick = {},
            )
        }
    }
}
