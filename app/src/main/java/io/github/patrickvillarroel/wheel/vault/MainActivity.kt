package io.github.patrickvillarroel.wheel.vault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.patrickvillarroel.wheel.vault.navigation.WheelVaultApp
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme
import org.koin.android.ext.android.inject

/**
 * Wheel Vault app
 * @author Rey Acosta 8-1024-1653
 * @author Patrick Villarroel E-8-206126
 * @author Derlin Romero 20-62-7741
 */
class MainActivity : ComponentActivity() {
    val supabase: SupabaseClient by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supabase.handleDeeplinks(intent)
        enableEdgeToEdge()
        setContent {
            WheelVaultTheme {
                WheelVaultApp()
            }
        }
    }
}
