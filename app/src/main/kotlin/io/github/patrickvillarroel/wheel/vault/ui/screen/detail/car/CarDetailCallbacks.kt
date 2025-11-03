package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car

import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks

class CarDetailCallbacks(
    val onEditClick: () -> Unit,
    val onDeleteClick: () -> Unit,
    val onFavoriteToggle: (Boolean) -> Unit,
    val onRefresh: () -> Unit,
    val onToggleTradeAvailabilityClick: () -> Unit,
    val headersBackCallbacks: HeaderBackCallbacks,
) {
    companion object {
        @JvmField
        val default = CarDetailCallbacks(
            onEditClick = {},
            onDeleteClick = {},
            onFavoriteToggle = {},
            onRefresh = {},
            onToggleTradeAvailabilityClick = {},
            headersBackCallbacks = HeaderBackCallbacks.default,
        )
    }
}
