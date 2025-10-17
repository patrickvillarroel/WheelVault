package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.decodeStream
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme
import io.github.patrickvillarroel.wheel.vault.util.resizeBitmapMaxDimension
import java.io.File
import java.io.IOException

@Composable
fun ModalAddImage(
    onResultGallery: (Bitmap) -> Unit,
    onModalClose: () -> Unit,
    onResultCamera: (Bitmap) -> Unit,
    isCameraPermission: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var photoFile: File? by remember { mutableStateOf(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            Log.i("ModalAddImage", "photoPickerLauncher result $uri")
            if (uri != null) {
                try {
                    val inputStream = requireNotNull(context.contentResolver.openInputStream(uri))
                    val originalBitmap = requireNotNull(inputStream.use { decodeStream(inputStream) })
                    val resizedUri = resizeBitmapMaxDimension(originalBitmap)
                    onResultGallery(resizedUri)
                } catch (e: Exception) {
                    Log.e("ModalAddImage", "Error procesando imagen de la galería", e)
                    Toast.makeText(context, "No se pudo procesar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
            onModalClose()
        },
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        Log.i("ModalAddImage", "cameraLauncher result ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK && photoFile != null) {
            try {
                val originalBitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                if (originalBitmap != null) {
                    val resizedBitmap = resizeBitmapMaxDimension(originalBitmap)
                    onResultCamera(resizedBitmap)
                } else {
                    Toast.makeText(context, "No se pudo cargar la foto.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ModalAddImage", "Error al procesar imagen de cámara", e)
                Toast.makeText(context, "Error al procesar la foto.", Toast.LENGTH_SHORT).show()
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
                        val file = File.createTempFile(
                            "camera_",
                            ".png",
                            context.cacheDir,
                        )
                        photoFile = file

                        val photoUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        }
                        cameraLauncher.launch(takePictureIntent)
                    } catch (e: IOException) {
                        Log.e("ModalAddImage", "Error al crear el archivo temporal", e)
                        Toast.makeText(context, "No se pudo crear el archivo.", Toast.LENGTH_SHORT).show()
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
