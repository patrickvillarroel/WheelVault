package io.github.patrickvillarroel.wheel.vault.ui.screen.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onLoginWithEmailAndPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO add logic with supabase, login with email means magic links
    LoginContent(
        onLoginWithEmailAndPasswordClick = onLoginWithEmailAndPasswordClick,
        onLoginWithEmailClick = onLoginSuccess,
        onLoginWithGoogleClick = onLoginSuccess,
        onRegisterClick = onRegisterClick,
        modifier = modifier,
    )
}
