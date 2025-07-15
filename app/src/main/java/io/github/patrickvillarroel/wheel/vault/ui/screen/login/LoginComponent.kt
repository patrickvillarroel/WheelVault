package io.github.patrickvillarroel.wheel.vault.ui.screen.login

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.providers.OAuthProvider
import io.github.jan.supabase.compose.auth.ui.ProviderIcon
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental

@OptIn(AuthUiExperimental::class)
@Composable
fun RowScope.ProviderButton(
    provider: OAuthProvider,
    modifier: Modifier = Modifier,
    text: String = "Continue with ${provider.name.replaceFirstChar { it.uppercase()} }",
) {
    ProviderIcon(provider, "Login with ${provider.name}", modifier.size(24.dp))
    Text(text, Modifier.weight(1f), textAlign = TextAlign.Center)
}
