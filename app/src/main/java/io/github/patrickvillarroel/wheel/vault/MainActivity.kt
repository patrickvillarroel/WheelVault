package io.github.patrickvillarroel.wheel.vault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.patrickvillarroel.wheel.vault.navigation.WheelVaultApp
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WheelVaultTheme {
                WheelVaultApp()
            }
        }
    }
}
