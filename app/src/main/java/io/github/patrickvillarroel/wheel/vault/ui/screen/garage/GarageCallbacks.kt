package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import java.util.UUID

data class GarageCallbacks(
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

    data class FilterBar(
        val onHomeClick: () -> Unit,
        val onFilterByBrand: (String) -> Unit,
        val onFilterByFavorite: (Boolean) -> Unit,
        val onSortByRecent: () -> Unit,
        val onSortByLast: () -> Unit,
    )

    data class Partial(
        val onHomeClick: () -> Unit,
        val onAddClick: () -> Unit,
        val onCarClick: (UUID) -> Unit,
        val onProfileClick: () -> Unit,
    )
}
