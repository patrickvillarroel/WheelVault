package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.github.patrickvillarroel.wheel.vault.util.rotateTo
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    onImageCaptureForAnalysis: (ImageProxy) -> Unit,
    onImageCapture: (Bitmap) -> Unit,
    triggerImageCapture: Boolean,
    triggerImageAnalysis: Boolean,
    modifier: Modifier = Modifier,
) {
    val onImageCaptureForAnalysisLatest by rememberUpdatedState(onImageCaptureForAnalysis)
    val onImageCaptureLatest by rememberUpdatedState(onImageCapture)
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(lifecycleOwner)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    LaunchedEffect(triggerImageCapture) {
        if (triggerImageCapture) {
            cameraController.takePicture(
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(output: ImageProxy) {
                        val rotation = output.imageInfo.rotationDegrees
                        val bitmap = output.toBitmap().rotateTo(rotation)
                        onImageCaptureLatest(bitmap)
                        output.close()
                    }

                    override fun onError(exc: ImageCaptureException) {
                        Log.e("Camera", "Error al capturar imagen", exc)
                    }
                },
            )
        }
    }

    DisposableEffect(triggerImageAnalysis) {
        cameraController.clearImageAnalysisAnalyzer()
        if (triggerImageAnalysis) {
            cameraController.setImageAnalysisAnalyzer(cameraExecutor, onImageCaptureForAnalysisLatest)
        }
        onDispose { cameraController.clearImageAnalysisAnalyzer() }
    }

    AndroidView(
        factory = { _ ->
            PreviewView(context).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                controller = cameraController
            }
        },
        modifier = modifier.fillMaxSize().padding(bottom = 100.dp),
        onRelease = {
            cameraExecutor.shutdown()
            it.controller = null
            it.removeAllViews()
        },
    )
}
