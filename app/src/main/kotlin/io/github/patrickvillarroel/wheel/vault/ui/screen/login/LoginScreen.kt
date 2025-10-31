package io.github.patrickvillarroel.wheel.vault.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.patrickvillarroel.wheel.vault.R
import org.koin.compose.viewmodel.koinViewModel

@Suppress("compose:multiple-emitters-check")
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
    var showDialog by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(true) {
        loginViewModel.resetState()
        showDialog = true
    }

    LoginContent(
        onLoginWithEmailAndPasswordClick = onLoginWithEmailAndPasswordClick,
        onLoginWithEmailClick = onLoginWithEmail,
        onLoginWithGoogleClick = { loginViewModel.login(context) },
        onRegisterClick = onRegisterClick,
        modifier = modifier,
    )

    when (uiState) {
        is LoginUiState.Success -> {
            onLoginSuccess()
            loginViewModel.resetState()
        }

        is LoginUiState.Loading -> {
            LoadingIndicator(Modifier.fillMaxSize())
        }

        is LoginUiState.Waiting -> Unit

        is LoginUiState.Error -> {
            if (showDialog) {
                Dialog(
                    onDismissRequest = {
                        showDialog = false
                        loginViewModel.resetState()
                    },
                ) {
                    Column(
                        Modifier.fillMaxSize(.5f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Image(
                            painterResource(R.drawable.error),
                            stringResource(R.string.error),
                            Modifier.padding(16.dp).fillMaxWidth(0.8f),
                        )
                        Text(
                            "$uiState",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
