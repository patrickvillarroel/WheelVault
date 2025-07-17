package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraLensScreen(
    onBack: () -> Unit,
    onAddDetail: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = koinViewModel(),
    permissionViewModel: CameraPermissionViewModel = koinViewModel(),
) {
    var isCameraPermission by rememberSaveable { mutableStateOf(false) }
    val recognizedText by viewModel.recognizedText.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    val showControls by viewModel.showControls.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { /* Maybe check the result, but we trust in permission state */ }

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            isCameraPermission = true
        } else {
            permissionViewModel.requestPermission()
        }
    }
    LaunchedEffect(Unit) {
        // Reset when this composable start in composition to avoid previous results on VM show on UI
        viewModel.reset()
    }

    if (permissionViewModel.showPermissionDialog) {
        CameraPermissionModal(
            onRequestPermission = {
                permissionViewModel.dismissDialog()
                permissionState.launchPermissionRequest()
            },
            onGoToSettings = {
                permissionViewModel.dismissDialog()
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null),
                )
                settingsLauncher.launch(intent)
            },
            onDismiss = { permissionViewModel.dismissDialog() },
            isPermanentlyDenied = !permissionState.status.shouldShowRationale && !permissionState.status.isGranted,
        )
    }

    CameraLensContent(
        recognizedText = recognizedText,
        isProcessing = isProcessing,
        showControls = showControls,
        isCameraPermission = isCameraPermission,
        callbacks = CameraCallbacks(
            onBack = onBack,
            onSkipClick = { onAddDetail(null) },
            processImage = { viewModel.processImage(it) },
            reset = { viewModel.reset() },
            onConfirm = { onAddDetail(recognizedText) },
            saveImage = { viewModel.saveImage(it) },
        ),
        modifier = modifier,
    )
}
