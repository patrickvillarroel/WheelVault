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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(dispatcher: CoroutineDispatcher = Dispatchers.Default) : ViewModel() {
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.RequestingPermission)
    val uiState = _uiState.asStateFlow()

    // For text recognition
    private val imageProxyFlow = MutableSharedFlow<ImageProxy>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    // Stores the latest recognized text and captured image
    @Volatile
    private var currentRecognizedText: String = ""

    @Volatile
    private var currentCapturedImage: Bitmap? = null

    // Signals to CameraPreview to take a picture
    private val _triggerImageCapture = MutableStateFlow(false)
    val triggerImageCapture = _triggerImageCapture.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            imageProxyFlow.collect { imageProxy ->
                processImageForTextInternal(imageProxy)
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageForTextInternal(imageProxy: ImageProxy) {
        if (_uiState.value !is CameraUiState.ProcessingText) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        textRecognizer.process(image)
            .addOnSuccessListener { visionText: Text ->
                val filtered = visionText.textBlocks.flatMap { it.lines }
                    .map { it.text.trim() }
                    .filter { it.length in 4..30 && it.matches("^[A-Z0-9\\s'-]+$".toRegex()) }
                    .joinToString(" ")

                if (filtered.isNotBlank() && _uiState.value is CameraUiState.ProcessingText) {
                    currentRecognizedText = filtered
                    _uiState.value = CameraUiState.ConfirmRecognizedText(filtered)
                }
            }
            .addOnFailureListener { e ->
                Log.e("CameraViewModel", "Error al reconocer texto", e)
                if (_uiState.value is CameraUiState.ProcessingText) {
                    _uiState.value = CameraUiState.ProcessingText("Error al reconocer texto.") // Show error temporarily
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun onPermissionGranted() {
        if (_uiState.value == CameraUiState.RequestingPermission) {
            _uiState.value = CameraUiState.ProcessingText("Apunte al nombre del modelo...")
        }
    }

    fun onPermissionDenied() {
        _uiState.value = CameraUiState.RequestingPermission
    }

    fun processImageForTextAnalysis(imageProxy: ImageProxy) {
        if (_uiState.value is CameraUiState.ProcessingText && !imageProxyFlow.tryEmit(imageProxy)) {
            imageProxy.close()
        }
    }

    fun onTextRecognitionConfirmed() {
        if (_uiState.value is CameraUiState.ConfirmRecognizedText) {
            _uiState.value = CameraUiState.CapturingImage(currentRecognizedText, null)
        }
    }

    fun onTextRecognitionRetry() {
        if (_uiState.value is CameraUiState.ConfirmRecognizedText || _uiState.value is CameraUiState.ProcessingText) {
            _uiState.value = CameraUiState.ProcessingText("Reintentando... Apunte al modelo.")
        }
    }

    fun onTakePictureRequest() {
        if (_uiState.value is CameraUiState.CapturingImage) {
            _triggerImageCapture.value = true
        }
    }

    fun onCapturedImageProvided(bitmap: Bitmap) {
        _triggerImageCapture.value = false
        if (_uiState.value is CameraUiState.CapturingImage) {
            currentCapturedImage = bitmap
            _uiState.value = CameraUiState.ConfirmCapturedImage(currentRecognizedText, bitmap)
        }
    }

    fun onCapturedImageConfirmation() {
        if (_uiState.value is CameraUiState.ConfirmCapturedImage && currentCapturedImage != null) {
            onAddDetail(currentRecognizedText, currentCapturedImage)
        }
    }

    fun onCapturedImageRetry() {
        if (_uiState.value is CameraUiState.ConfirmCapturedImage) {
            currentCapturedImage = null
            _uiState.value = CameraUiState.CapturingImage(currentRecognizedText, null)
        }
    }

    fun onSkip() {
        when (_uiState.value) {
            is CameraUiState.ProcessingText, is CameraUiState.ConfirmRecognizedText -> {
                currentRecognizedText = ""
                _uiState.value = CameraUiState.CapturingImage(currentRecognizedText, null)
            }
            is CameraUiState.CapturingImage, is CameraUiState.ConfirmCapturedImage -> {
                currentCapturedImage = null
                onAddDetail(currentRecognizedText, null)
            }
            else -> Unit
        }
    }

    /**
     * Handles back navigation.
     * @return Returns true if back navigation is needed, false otherwise.
     */
    fun onBack(): Boolean {
        when (_uiState.value) {
            is CameraUiState.ConfirmCapturedImage ->
                _uiState.value = CameraUiState.CapturingImage(currentRecognizedText, null)
            is CameraUiState.CapturingImage ->
                _uiState.value = CameraUiState.ConfirmRecognizedText(currentRecognizedText)
            is CameraUiState.ConfirmRecognizedText ->
                _uiState.value = CameraUiState.ProcessingText("Apunte al nombre del modelo...")
            is CameraUiState.ProcessingText -> return true
            is CameraUiState.Completed -> return true
            else -> return true
        }
        return false
    }

    fun init() {
        reset()
        resetImage()
    }

    fun reset() {
        _uiState.update { CameraUiState.RequestingPermission }
        currentRecognizedText = ""
        _triggerImageCapture.value = false
    }

    fun getCapturedImage(): Bitmap? = currentCapturedImage

    fun resetImage() {
        currentCapturedImage = null
    }

    private fun onAddDetail(text: String, image: Bitmap?) {
        Log.d("CameraViewModel", "onAddDetail called with Text: $text, Image: ${image != null}")
        _uiState.value = CameraUiState.Completed(text.trim(), image)
    }

    override fun onCleared() {
        super.onCleared()
        textRecognizer.close()
    }
}
