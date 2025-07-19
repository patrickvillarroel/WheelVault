package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CameraViewModel : ViewModel() {
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val _recognizedText = MutableStateFlow("Apunte al nombre del modelo...")
    val recognizedText = _recognizedText.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _showControls = MutableStateFlow(false)
    val showControls = _showControls.asStateFlow()

    @Volatile
    private var hasRecognizedValidText = false

    private val imageProxyFlow = MutableSharedFlow<ImageProxy>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    init {
        viewModelScope.launch {
            imageProxyFlow
                .buffer()
                .collect { imageProxy ->
                    withContext(Dispatchers.Default) {
                        processImageInternal(imageProxy)
                    }
                }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageInternal(imageProxy: ImageProxy) {
        if (hasRecognizedValidText) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        _isProcessing.update { true }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        textRecognizer.process(image)
            .addOnSuccessListener { visionText: Text ->
                val filtered = visionText.textBlocks.flatMap { it.lines }
                    .map { it.text.trim() }
                    .filter {
                        it.length in 4..30 && it.matches("^[A-Z0-9\\s'-]+$".toRegex())
                    }.joinToString(" ")

                if (filtered.isNotBlank()) {
                    _recognizedText.update { filtered }
                    _showControls.update { true }
                    hasRecognizedValidText = true
                }
                _isProcessing.update { false }
            }
            .addOnFailureListener { e ->
                Log.e("Camera View Model", "Error al reconocer texto", e)
                _recognizedText.update { "Error al reconocer texto." }
                _showControls.update { false }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun reset() {
        _recognizedText.update { "Apunte al nombre del modelo..." }
        _showControls.update { false }
        _isProcessing.update { true }
        hasRecognizedValidText = false
    }

    fun processImage(imageProxy: ImageProxy) {
        if (!imageProxyFlow.tryEmit(imageProxy)) {
            imageProxy.close()
        }
    }

    fun saveImage(imageBytes: Bitmap) {
        /* TODO */
    }

    override fun onCleared() {
        super.onCleared()
        textRecognizer.close()
    }
}
