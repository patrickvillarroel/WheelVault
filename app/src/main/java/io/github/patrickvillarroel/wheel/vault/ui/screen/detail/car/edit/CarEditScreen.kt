package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val detailState by carViewModel.carDetailState.collectAsStateWithLifecycle()
    var initial by remember(partialCarItem) {
        mutableStateOf(
            if (detailState is CarViewModel.CarDetailUiState.Success &&
                (detailState as CarViewModel.CarDetailUiState.Success).car.id == partialCarItem.id
            ) {
                // Recover the full state of the car because navigation don't preserve network images, only links (strings)
                (detailState as CarViewModel.CarDetailUiState.Success).car.toPartial()
            } else {
                partialCarItem
            },
        )
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
            Log.i("CarEditScreen", "onAddPictureClick, current: $initial, new state: $it")
            // Receive the current status of the car, after the picture is added re-assign with copy
            initial = it
        },
        onConfirmClick = {
            carViewModel.save(it, context)
            headersBackCallbacks.onBackClick()
        },
        isEditAction = partialCarItem.id != null,
        headersBackCallbacks = headersBackCallbacks,
        modifier = modifier,
    )

    if (openBottomSheet) {
        ModalAddImage(
            onResultGallery = {
                Log.i("CarEditScreen", "onResult, current: $initial, new state with image: $it")
                initial = initial.copy(images = setOf(it.toCoilUri()) + initial.images)
            },
            onModalClose = {
                openBottomSheet = false
            },
            onResultCamera = {
                Log.i("CarEditScreen", "onResultCamera, current: $initial, new state with camera: $it")
                initial = initial.copy(images = setOf(it) + initial.images)
            },
            isCameraPermission = permissionState.status.isGranted,
        )
    }
}
