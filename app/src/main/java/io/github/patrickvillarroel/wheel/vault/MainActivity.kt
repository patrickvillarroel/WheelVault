package io.github.patrickvillarroel.wheel.vault

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.patrickvillarroel.wheel.vault.ui.screen.home.CollectorsHomeScreen
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.dark(Color.argb(0x80, 0x1b, 0x1b, 0x1b)))
        setContent {
            WheelVaultTheme {
                CollectorsHomeScreen()
            }
        }
    }
}
