package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem

@Composable
fun CarEditScreen(
    partialCarItem: CarItem.Partial?,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onGarageClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CarEditContent(
        carDetailPartial = remember(partialCarItem) { partialCarItem ?: CarItem.Partial() },
        onBackClick = onBackClick,
        onProfileClick = onProfileClick,
        onGarageClick = onGarageClick,
        onFavoritesClick = onFavoritesClick,
        onStatisticsClick = onStatisticsClick,
        onAddPictureClick = { it },
        onConfirmClick = {
            /* TODO change to use VM */
            onBackClick()
        },
        isEditAction = partialCarItem != null,
        modifier = modifier,
    )
}
