package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks

data class GarageCallbacks(
    val onHomeClick: () -> Unit,
    val onSearch: (String) -> Unit,
    val onAddClick: () -> Unit,
    val onCarClick: (CarItem) -> Unit,
    val onToggleFavorite: (CarItem, Boolean) -> Unit,
    val headersCallbacks: HeaderCallbacks,
)
