package io.github.patrickvillarroel.wheel.vault.ui.screen.login

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginWithEmailScreen(
    onLoginSuccess: () -> Unit,
    isRegister: Boolean,
    isMagicLink: Boolean,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = koinViewModel(),
) {
    val uiState by loginViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (uiState) {
        is LoginUiState.Success -> {
            onLoginSuccess()
        }

        is LoginUiState.Loading -> {
            // Show loading indicator
        }

        is LoginUiState.Error -> {
            // TODO
            Toast.makeText(context, "Error $uiState", Toast.LENGTH_SHORT).show()
        }
    }

    LoginWithEmailContent(
        onClick = { email, password ->
            if (isRegister) {
                loginViewModel.register(email, password)
            } else if (isMagicLink) {
                loginViewModel.login(email)
            } else {
                loginViewModel.login(email, password)
            }
        },
        isRegister = isRegister,
        isMagicLink = isMagicLink,
        modifier = modifier,
    )
}
