package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    // Estado editable del formulario
    var editableCar by remember { mutableStateOf(partialCarItem) }

    /*
     * Lógica de inicialización:
     * - Si es desde cámara → agrega la imagen capturada.
     * - Si tiene ID (editar) → pide al VM que cargue el detalle completo.
     * - Si no tiene ID → queda el builder parcial.
     */
    LaunchedEffect(Unit) {
        brandViewModel.fetchNames()

        if (fromCamera) {
            logger.d { "Initializing from camera" }
            cameraViewModel.getCapturedImage()?.let { captured ->
                editableCar = editableCar.copy(images = setOf(captured) + editableCar.images)
            }
            cameraViewModel.resetImage()
        } else if (partialCarItem.id != null) {
            logger.d { "Loading car from ViewModel by ID=${partialCarItem.id}" }
            carViewModel.findById(partialCarItem.id)
        }
    }

    /*
     * Cuando el ViewModel carga el auto, sincronizamos el estado editable.
     * Solo ocurre en edición (cuando se obtuvo el detalle con éxito).
     */
    LaunchedEffect(detailState) {
        val snapshot = detailState
        if (snapshot is CarViewModel.CarDetailUiState.Success) {
            val car = snapshot.car
            editableCar = car.toBuilder()
            logger.d { "Loaded car from VM with id='${car.id}'" }
        }
    }

    // Navegación automática tras guardado exitoso.
    LaunchedEffect(detailState, shouldNavigateBack) {
        if (shouldNavigateBack && detailState is CarViewModel.CarDetailUiState.Success) {
            logger.v("Save successful, navigating back")
            headersBackCallbacks.onBackClick()
            shouldNavigateBack = false
        }
    }

    // --- UI principal ---
    CarEditContent(
        initial = editableCar,
        onAddPictureClick = {
            openBottomSheet = true
        },
        onConfirmClick = {
            logger.d { "Saving car: $it" }
            carViewModel.save(it, context)
            shouldNavigateBack = true
        },
        isEditAction = partialCarItem.id != null,
        headersBackCallbacks = headersBackCallbacks,
        manufacturerList = brandsNames.ifEmpty {
            logger.e { "Brands names is empty, fallback in UI to prevent exceptions" }
            listOf("Otros")
        },
        modifier = modifier,
    )

    // --- Modal de añadir imagen ---
    if (openBottomSheet) {
        ModalAddImage(
            onResultGallery = { image ->
                editableCar = editableCar.copy(images = setOf(image) + editableCar.images)
                openBottomSheet = false
            },
            onResultCamera = { image ->
                editableCar = editableCar.copy(images = setOf(image) + editableCar.images)
                openBottomSheet = false
            },
            onModalClose = { openBottomSheet = false },
            isCameraPermission = !missingPermissions,
        )
    }
}
