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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun ModalAddImage(
    onResultGallery: (Uri) -> Unit,
    onModalClose: () -> Unit,
    onResultCamera: (Bitmap) -> Unit,
    isCameraPermission: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            Log.i("ModalAddImage", "photoPickerLauncher result $uri")
            if (uri != null) {
                onResultGallery(uri)
            }
            onModalClose()
        },
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        Log.i("ModalAddImage", "cameraLauncher result ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            Log.i("ModalAddImage", "cameraLauncher result data ${result.data}")
            val imageBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.extras?.getParcelable("data", Bitmap::class.java)
            } else {
                @Suppress("DEPRECATED", "kotlin:S1874", "DEPRECATION")
                result.data?.extras?.getParcelable("data")
            }
            if (imageBitmap != null) {
                onResultCamera(imageBitmap)
            }
        }
        onModalClose()
    }

    ModalBottomSheet(onDismissRequest = onModalClose, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(stringResource(R.string.select_image), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    try {
                        Log.i("ModalAddImage", "takePictureIntent")
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        cameraLauncher.launch(takePictureIntent)
                    } catch (e: ActivityNotFoundException) {
                        Log.e("ModalAddImage", "Error al lanzar la cámara", e)
                        Toast.makeText(context, "La cámara no esta disponible.", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isCameraPermission,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.from_camera))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    Log.i("ModalAddImage", "photoPickerLauncher")
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.from_gallery))
            }
        }
    }
}

@Preview
@Composable
private fun ModalPreview() {
    WheelVaultTheme {
        ModalAddImage(onResultGallery = {}, onModalClose = {}, onResultCamera = {}, isCameraPermission = false)
    }
}
