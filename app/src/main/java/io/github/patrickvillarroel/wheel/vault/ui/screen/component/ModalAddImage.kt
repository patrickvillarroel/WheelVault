package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun ModalAddImage(
    onResult: (Uri) -> Unit,
    onModalClose: () -> Unit,
    onResultCamera: (Bitmap) -> Unit,
    isCameraPermission: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onResult(uri)
            } else {
                onModalClose()
            }
        },
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.extras?.getParcelable("data", Bitmap::class.java)
            } else {
                @Suppress("DEPRECATED", "kotlin:S1874", "DEPRECATION")
                result.data?.extras?.getParcelable("data")
            }
            if (imageBitmap != null) {
                onResultCamera(imageBitmap)
            } else {
                onModalClose()
            }
        }
    }

    ModalBottomSheet(onDismissRequest = onModalClose, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text("Seleccionar Imagen", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    try {
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        cameraLauncher.launch(takePictureIntent)
                    } catch (e: ActivityNotFoundException) {
                        Log.e("ModalAddImage", "Error al lanzar la cámara", e)
                        Toast.makeText(context, "La cámara no esta disponible.", Toast.LENGTH_SHORT).show()
                    } finally {
                        onModalClose()
                    }
                },
                enabled = isCameraPermission,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Desde Cámara")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                    onModalClose()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Desde Galería")
            }
        }
    }
}

@Preview
@Composable
private fun ModalPreview() {
    WheelVaultTheme {
        ModalAddImage(onResult = {}, onModalClose = {}, onResultCamera = {}, isCameraPermission = false)
    }
}
