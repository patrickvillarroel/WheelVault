package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.toCoilUri
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.ModalAddImage
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CarEditScreen(
    partialCarItem: CarItem.Partial,
    headersBackCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel = koinViewModel(),
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val detailState by carViewModel.carDetailState.collectAsStateWithLifecycle()
    var initial = remember(partialCarItem) {
        if (detailState is CarViewModel.CarDetailUiState.Success &&
            (detailState as CarViewModel.CarDetailUiState.Success).car.id == partialCarItem.id
        ) {
            // Recover the full state of the car because navigation don't preserve network images, only links (strings)
            (detailState as CarViewModel.CarDetailUiState.Success).car.toPartial()
        } else {
            partialCarItem
        }
    }

    DisposableEffect(partialCarItem.id) {
        if (partialCarItem.id != null) {
            carViewModel.findById(partialCarItem.id)
        }
        onDispose {
            openBottomSheet = false
        }
    }

    CarEditContent(
        initial = initial,
        onAddPictureClick = {
            openBottomSheet = true
            // Receive the current status of the car, after the picture is added re-assign with copy
            initial = it
        },
        onConfirmClick = {
            carViewModel.save(it)
            headersBackCallbacks.onBackClick()
        },
        isEditAction = partialCarItem.id != null,
        headersBackCallbacks = headersBackCallbacks,
        modifier = modifier,
    )

    if (openBottomSheet) {
        ModalAddImage(
            // TODO todavia no funka cuando se agregan nuevas imagenes
            onResult = {
                initial = initial.copy(images = initial.images + it.toCoilUri())
                openBottomSheet = false
            },
            onModalClose = {
                openBottomSheet = false
            },
            onResultCamera = {
                initial = initial.copy(images = initial.images + it)
                openBottomSheet = false
            },
            isCameraPermission = permissionState.status.isGranted,
        )
    }
}
