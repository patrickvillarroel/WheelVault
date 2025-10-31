package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.camera.CameraViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.ModalAddImage
import org.koin.compose.viewmodel.koinViewModel

private val logger = Logger.withTag("CarEditScreen")

@Composable
fun CarEditScreen(
    partialCarItem: CarItem.Builder,
    fromCamera: Boolean,
    headersBackCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
    brandViewModel: BrandViewModel = koinViewModel(),
    carViewModel: CarViewModel = koinViewModel(),
    cameraViewModel: CameraViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val missingPermissions = rememberSaveable {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED
    }
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var shouldNavigateBack by rememberSaveable { mutableStateOf(false) }
    val detailState by carViewModel.carDetailState.collectAsStateWithLifecycle()
    val brandsNames by brandViewModel.brandsNames.collectAsStateWithLifecycle()
    var initial by remember(partialCarItem) {
        val stateSnapshot = detailState
        mutableStateOf(
            if (stateSnapshot is CarViewModel.CarDetailUiState.Success && stateSnapshot.car.id == partialCarItem.id) {
                logger.d { "Recovering state from car detail" }
                // Recover the full state of the car because navigation don't preserve network images, only links (strings)
                stateSnapshot.car.toBuilder()
            } else if (fromCamera) {
                logger.d { "Recovering state from camera, cleaning camera view model" }
                val capturedImage = cameraViewModel.getCapturedImage()
                cameraViewModel.resetImage()
                partialCarItem.copy(images = setOfNotNull(capturedImage) + partialCarItem.images)
            } else {
                logger.d { "Recovering state from navigation" }
                // WARNING: only have images of strings (links) because limitations of navigation serialization
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

    // Navigate back when save is successful
    DisposableEffect(detailState, shouldNavigateBack) {
        if (shouldNavigateBack && detailState is CarViewModel.CarDetailUiState.Success) {
            logger.v("Save successful, navigating back")
            headersBackCallbacks.onBackClick()
            shouldNavigateBack = false
        }
        onDispose { }
    }

    CarEditContent(
        initial = initial,
        onAddPictureClick = {
            openBottomSheet = true
            logger.d { "onAddPictureClick, current: $initial, new state: $it" }
            // Receive the current status of the car, after the picture is added re-assign with copy
            initial = it
        },
        onConfirmClick = {
            logger.d { "Saving car: $it" }
            carViewModel.save(it, context)
            shouldNavigateBack = true
        },
        isEditAction = partialCarItem.id != null,
        headersBackCallbacks = headersBackCallbacks,
        manufacturerList = brandsNames,
        modifier = modifier,
    )

    if (openBottomSheet) {
        ModalAddImage(
            onResultGallery = {
                logger.d { "onResult, current: $initial, new state with image: $it" }
                initial = initial.copy(images = setOf(it) + initial.images)
            },
            onModalClose = {
                openBottomSheet = false
            },
            onResultCamera = {
                logger.d { "onResultCamera, current: $initial, new state with camera: $it" }
                initial = initial.copy(images = setOf(it) + initial.images)
            },
            isCameraPermission = !missingPermissions,
        )
    }
}
