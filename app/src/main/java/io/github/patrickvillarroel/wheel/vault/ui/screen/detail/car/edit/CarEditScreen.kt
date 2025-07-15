package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks

@Composable
fun CarEditScreen(
    partialCarItem: CarItem.Partial?,
    headersBackCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
) {
    CarEditContent(
        carDetailPartial = remember(partialCarItem) { partialCarItem ?: CarItem.Partial() },
        onConfirmClick = {
            /* TODO change to use VM */
            headersBackCallbacks.onBackClick()
        },
        isEditAction = partialCarItem != null,
        headersBackCallbacks = headersBackCallbacks,
        modifier = modifier,
    )
}
