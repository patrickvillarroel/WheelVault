package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CameraPermissionViewModel : ViewModel() {
    var showPermissionDialog by mutableStateOf(false)
        private set

    fun requestPermission() {
        showPermissionDialog = true
    }

    fun dismissDialog() {
        showPermissionDialog = false
    }
}
