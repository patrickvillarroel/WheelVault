package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun CameraPermissionModal(
    onRequestPermission: () -> Unit,
    onGoToSettings: () -> Unit,
    onDismiss: () -> Unit,
    isPermanentlyDenied: Boolean,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
    ) {
        Column(
            Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Default.PhotoCamera,
                stringResource(R.string.camera_permission),
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(R.string.camera_permission_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.camera_permission_description),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (isPermanentlyDenied) {
                Button(onClick = onGoToSettings) {
                    Text(stringResource(R.string.open_settings))
                }
            } else {
                Button(onClick = onRequestPermission) {
                    Text(stringResource(R.string.request_permission))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CameraPermissionDialogPreview() {
    WheelVaultTheme {
        CameraPermissionModal(
            onRequestPermission = {},
            onGoToSettings = {},
            onDismiss = {},
            isPermanentlyDenied = false,
        )
    }
}
