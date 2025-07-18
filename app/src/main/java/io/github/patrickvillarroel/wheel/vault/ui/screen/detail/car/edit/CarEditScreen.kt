package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CarEditScreen(
    partialCarItem: CarItem.Partial,
    headersBackCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel = koinViewModel(),
) {
    val detailState by carViewModel.carDetailState.collectAsStateWithLifecycle()
    val initial = if (detailState is CarViewModel.CarDetailUiState.Success &&
        (detailState as CarViewModel.CarDetailUiState.Success).car.id == partialCarItem.id
    ) {
        // Recover the full state of the car because navigation don't preserve network images, only links (strings)
        (detailState as CarViewModel.CarDetailUiState.Success).car.toPartial()
    } else {
        partialCarItem
    }

    LaunchedEffect(partialCarItem.id) {
        if (partialCarItem.id != null) {
            carViewModel.findById(partialCarItem.id)
        }
    }

    CarEditContent(
        initial = initial,
        onConfirmClick = {
            carViewModel.save(it)
            headersBackCallbacks.onBackClick()
        },
        isEditAction = partialCarItem.id != null,
        headersBackCallbacks = headersBackCallbacks,
        modifier = modifier,
    )
}
