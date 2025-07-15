package io.github.patrickvillarroel.wheel.vault.ui.screen.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, modifier: Modifier = Modifier) {
    LoginContent(
        onLoginClick = {
            // TODO: Implement login logic with VM and supabase kt
            onLoginSuccess()
        },
        modifier = modifier,
    )
}
