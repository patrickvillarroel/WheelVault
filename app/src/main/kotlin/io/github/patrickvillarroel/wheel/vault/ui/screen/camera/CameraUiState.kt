package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import android.graphics.Bitmap

sealed interface CameraUiState {
    data object RequestingPermission : CameraUiState
    data class ProcessingText(val instructionText: String) : CameraUiState
    data class ConfirmRecognizedText(val recognizedText: String) : CameraUiState
    data class CapturingImage(val confirmedText: String, val lastImageAttempt: Bitmap?) : CameraUiState
    data class ConfirmCapturedImage(val confirmedText: String, val capturedImage: Bitmap) : CameraUiState
    data class Completed(val currentText: String?, val currentImage: Bitmap?) : CameraUiState
}
