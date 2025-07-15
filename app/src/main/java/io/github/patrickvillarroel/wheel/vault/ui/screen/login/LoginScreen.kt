package io.github.patrickvillarroel.wheel.vault.ui.screen.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onLoginWithEmailClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LoginContent(
        onLoginWithEmailClick = onLoginWithEmailClick,
        onLoginWithGoogleClick = onLoginSuccess,
        onRegisterClick = onRegisterClick,
        modifier = modifier,
    )
}
