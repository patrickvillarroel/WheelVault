package io.github.patrickvillarroel.wheel.vault.ui.screen.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onLoginWithEmail: () -> Unit,
    onLoginWithEmailAndPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = koinViewModel(),
) {
    val uiState by loginViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (uiState is LoginUiState.Success) {
        onLoginSuccess()
    }

    LoginContent(
        onLoginWithEmailAndPasswordClick = onLoginWithEmailAndPasswordClick,
        onLoginWithEmailClick = onLoginWithEmail,
        onLoginWithGoogleClick = {
            loginViewModel.login(context)
        },
        onRegisterClick = onRegisterClick,
        modifier = modifier,
    )
}
