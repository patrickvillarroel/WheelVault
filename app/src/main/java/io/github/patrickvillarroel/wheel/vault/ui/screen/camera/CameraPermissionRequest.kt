package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionRequest(
    showPermissionDialog: Boolean,
    onDismissDialog: () -> Unit,
    onRequestPermission: () -> Unit,
    isCameraPermissionGranted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val onDismissDialogLatest by rememberUpdatedState(onDismissDialog)
    val onRequestPermissionLatest by rememberUpdatedState(onRequestPermission)
    val isCameraPermissionGrantedLatest by rememberUpdatedState(isCameraPermissionGranted)
    val context = LocalContext.current
    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { /* Maybe check the result, but we trust in permission state */ }

    DisposableEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            isCameraPermissionGrantedLatest()
        } else {
            onRequestPermissionLatest()
        }

        onDispose {
            onDismissDialogLatest()
        }
    }

    if (showPermissionDialog) {
        CameraPermissionModal(
            onRequestPermission = {
                onDismissDialog()
                permissionState.launchPermissionRequest()
            },
            onGoToSettings = {
                onDismissDialog()
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null),
                )
                settingsLauncher.launch(intent)
            },
            onDismiss = onDismissDialog,
            isPermanentlyDenied = !permissionState.status.shouldShowRationale && !permissionState.status.isGranted,
            modifier = modifier,
        )
    }
}
