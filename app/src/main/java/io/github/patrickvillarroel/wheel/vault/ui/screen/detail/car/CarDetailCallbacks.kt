package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car

import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks

class CarDetailCallbacks(
    val carDetail: CarItem,
    val onEditClick: () -> Unit,
    val onDeleteClick: () -> Unit,
    val onFavoriteToggle: (Boolean) -> Unit,
    val onRefresh: () -> Unit,
    val headersBackCallbacks: HeaderBackCallbacks,
) {
    companion object {
        fun default(carItem: CarItem) = CarDetailCallbacks(carItem, {}, {}, {}, {}, HeaderBackCallbacks.default)
    }
}
