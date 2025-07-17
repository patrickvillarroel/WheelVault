package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.github.patrickvillarroel.wheel.vault.util.createTempFileForImage
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    onImageCaptureForAnalysis: (ImageProxy) -> Unit,
    onImageCapture: (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER

                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

                cameraProviderFuture.addListener({
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = surfaceProvider
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

                    val outputOptions = ImageCapture.OutputFileOptions.Builder(createTempFileForImage(context)).build()
                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                val file = output.savedUri?.let { File(it.path!!) } ?: return
                                val bytes = file.readBytes()
                                onImageCapture(bytes)
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
            }
        },
        modifier = modifier.fillMaxSize().padding(bottom = 100.dp),
        onRelease = {
            it.controller = null
            it.removeAllViews()
        },
    )
}
