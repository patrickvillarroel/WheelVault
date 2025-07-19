package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import java.util.UUID

data class GarageCallbacks(
    val onHomeClick: () -> Unit,
    val onSearchQueryChange: (String) -> Unit,
    val onAddClick: () -> Unit,
    val onCarClick: (CarItem) -> Unit,
    val onToggleFavorite: (CarItem, Boolean) -> Unit,
    val onRefresh: () -> Unit,
    val onUiStateChange: (GarageUiState) -> Unit,
    val onSearchClick: () -> Unit,
    val headersCallbacks: HeaderCallbacks,
) {
    data class Partial(
        val onHomeClick: () -> Unit,
        val onAddClick: () -> Unit,
        val onCarClick: (UUID) -> Unit,
        val onProfileClick: () -> Unit,
    )
}
