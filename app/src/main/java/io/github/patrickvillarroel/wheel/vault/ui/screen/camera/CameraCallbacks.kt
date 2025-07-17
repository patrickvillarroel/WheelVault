package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import androidx.camera.core.ImageProxy

data class CameraCallbacks(
    val reset: () -> Unit,
    val onConfirm: () -> Unit,
    val onBack: () -> Unit,
    val onSkipClick: () -> Unit,
    val processImage: (ImageProxy) -> Unit,
    val saveImage: (ByteArray) -> Unit,
)
