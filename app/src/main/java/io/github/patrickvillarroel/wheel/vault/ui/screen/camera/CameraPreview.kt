package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    onImageCaptureForAnalysis: (ImageProxy) -> Unit,
    onImageCapture: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        factory = { _ ->
            val previewView = PreviewView(context).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            cameraProviderFuture.addListener({
                val preview = Preview.Builder().build().apply {
                    surfaceProvider = previewView.surfaceProvider
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, onImageCaptureForAnalysis)
                    }

                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                imageCapture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(output: ImageProxy) {
                            onImageCapture(output.toBitmap())
                        }

                        override fun onError(exc: ImageCaptureException) {
                            Log.e("Camera", "Error al capturar imagen", exc)
                        }
                    },
                )

                cameraProviderFuture.get().unbindAll()
                cameraProviderFuture.get().bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer,
                    imageCapture,
                )
            }, ContextCompat.getMainExecutor(context))
            previewView
        },
        modifier = modifier.fillMaxSize().padding(bottom = 100.dp),
        onRelease = {
            cameraProviderFuture.get().unbindAll()
            cameraExecutor.shutdown()
            it.controller = null
            it.removeAllViews()
        },
    )
}
