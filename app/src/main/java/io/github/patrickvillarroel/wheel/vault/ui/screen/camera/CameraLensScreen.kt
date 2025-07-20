package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CameraLensScreen(
    onBack: () -> Unit,
    onAddDetail: (String?, Bitmap?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val triggerImageCapture by viewModel.triggerImageCapture.collectAsState()
    var showPermissionModal by remember { mutableStateOf(false) }

    val callbacks = remember {
        CameraCallbacks(
            onBack = {
                if (viewModel.onBack()) {
                    onBack()
                }
            },
            onSkip = viewModel::onSkip,
            processImageForTextAnalysis = viewModel::processImageForTextAnalysis,
            onTextRecognitionConfirmed = viewModel::onTextRecognitionConfirmed,
            onTextRecognitionRetry = viewModel::onTextRecognitionRetry,
            onTakePictureRequest = viewModel::onTakePictureRequest,
            onCapturedImageProvided = viewModel::onCapturedImageProvided,
            onCapturedImageConfirmation = viewModel::onCapturedImageConfirmation,
            onCapturedImageRetry = viewModel::onCapturedImageRetry,
            onAddDetail = onAddDetail,
        )
    }

    DisposableEffect(true) {
        viewModel.init()
        if (uiState is CameraUiState.RequestingPermission) {
            showPermissionModal = true
        }
        onDispose {
            viewModel.reset()
        }
    }

    CameraLensContent(
        uiState = uiState,
        callbacks = callbacks,
        triggerImageCapture = triggerImageCapture,
        modifier = modifier,
    )

    CameraPermissionRequest(
        showPermissionDialog = showPermissionModal,
        onDismissDialog = { showPermissionModal = false },
        onRequestPermission = viewModel::onPermissionDenied,
        isCameraPermissionGranted = {
            showPermissionModal = false
            viewModel.onPermissionGranted()
        },
    )
}
