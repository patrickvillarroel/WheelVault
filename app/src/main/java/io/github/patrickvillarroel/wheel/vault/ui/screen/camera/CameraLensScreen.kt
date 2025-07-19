package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CameraLensScreen(
    onBack: () -> Unit,
    onAddDetail: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = koinViewModel(),
) {
    var isCameraPermission by rememberSaveable { mutableStateOf(false) }
    val recognizedText by viewModel.recognizedText.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    val showControls by viewModel.showControls.collectAsStateWithLifecycle()
    var showPermissionDialog by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(true) {
        // Reset when this composable start in composition to avoid previous results on VM show on UI
        viewModel.reset()
        onDispose {
            viewModel.reset()
        }
    }

    CameraPermissionRequest(
        showPermissionDialog = showPermissionDialog,
        onDismissDialog = { showPermissionDialog = false },
        onRequestPermission = { showPermissionDialog = true },
        isCameraPermissionGranted = { isCameraPermission = true },
    )

    CameraLensContent(
        recognizedText = recognizedText,
        isProcessing = isProcessing,
        showControls = showControls,
        isCameraPermission = isCameraPermission,
        callbacks = CameraCallbacks(
            onBack = onBack,
            onSkipClick = { onAddDetail(null) },
            processImage = viewModel::processImage,
            reset = viewModel::reset,
            onConfirm = { onAddDetail(recognizedText) },
            saveImage = viewModel::saveImage,
        ),
        modifier = modifier,
    )
}
