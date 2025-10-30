package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

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
    val activity = LocalActivity.current
    val context = activity ?: LocalContext.current
    val missingPermissions = rememberSaveable {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED
    }
    var permanentlyDeniedPermission by rememberSaveable { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            isCameraPermissionGrantedLatest()
        } else {
            if (activity != null &&
                ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.CAMERA)
            ) {
                onRequestPermissionLatest()
            } else {
                permanentlyDeniedPermission = true
            }
        }
    }

    DisposableEffect(missingPermissions) {
        if (missingPermissions) {
            onRequestPermissionLatest()
        } else {
            isCameraPermissionGrantedLatest()
        }
        onDispose {
            onDismissDialogLatest()
        }
    }

    if (showPermissionDialog) {
        CameraPermissionModal(
            onRequestPermission = {
                onDismissDialog()
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            },
            onGoToSettings = {
                onDismissDialog()
                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null),
                    ),
                )
            },
            onDismiss = onDismissDialog,
            isPermanentlyDenied = permanentlyDeniedPermission,
            modifier = modifier,
        )
    }
}
