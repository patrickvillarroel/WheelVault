package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy

data class CameraCallbacks(
    val onBack: () -> Unit,
    val onSkip: () -> Unit,

    // Text Recognition
    val processImageForTextAnalysis: (ImageProxy) -> Unit,
    val onTextRecognitionConfirmed: () -> Unit,
    val onTextRecognitionRetry: () -> Unit,

    // Image Capture
    val onTakePictureRequest: () -> Unit,
    val onCapturedImageProvided: (Bitmap) -> Unit,
    val onCapturedImageConfirmation: () -> Unit,
    val onCapturedImageRetry: () -> Unit,

    val onAddDetail: (String?, Bitmap?) -> Unit,
)
