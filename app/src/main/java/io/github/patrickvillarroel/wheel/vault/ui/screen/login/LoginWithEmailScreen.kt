package io.github.patrickvillarroel.wheel.vault.ui.screen.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoginWithEmailScreen(onLoginSuccess: () -> Unit, isRegister: Boolean, modifier: Modifier = Modifier) {
    LoginWithEmailContent(
        onLoginClick = {
            // TODO: Implement login logic with VM and supabase kt
            onLoginSuccess()
        },
        isRegister = isRegister,
        modifier = modifier,
    )
}
