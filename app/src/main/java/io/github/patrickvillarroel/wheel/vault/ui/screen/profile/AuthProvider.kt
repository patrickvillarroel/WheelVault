package io.github.patrickvillarroel.wheel.vault.ui.screen.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.compose.auth.ui.providerPainter
import androidx.compose.material3.Icon as MaterialIcon
import io.github.jan.supabase.auth.providers.Google as ProviderGoogle

sealed class AuthProvider {
    open val icon: ImageVector? = null

    @Stable
    @Composable
    open fun painter(): Painter? = null

    abstract val name: String

    @Stable
    @Composable
    fun Icon(
        modifier: Modifier = Modifier,
        contentDescription: String? = name,
        tint: Color = LocalContentColor.current,
    ) {
        icon?.let {
            MaterialIcon(imageVector = it, contentDescription = contentDescription, modifier = modifier, tint = tint)
        } ?: run {
            painter()?.let {
                MaterialIcon(painter = it, contentDescription = contentDescription, modifier = modifier, tint = tint)
            }
        }
    }

    @Stable
    object Email : AuthProvider() {
        override val icon: ImageVector = Icons.Filled.Email
        override val name: String = "Email"
    }

    @Stable
    object Password : AuthProvider() {
        override val icon: ImageVector = Icons.Filled.Password
        override val name: String = "Email and password"
    }

    @Stable
    object Google : AuthProvider() {
        override val name: String = "Google"

        @OptIn(SupabaseInternal::class)
        @Stable
        @Composable
        override fun painter(): Painter? = providerPainter(ProviderGoogle, LocalDensity.current)
    }

    @Stable
    companion object {
        val entries: List<AuthProvider>
            get() = listOf(Email, Password, Google)
    }
}
