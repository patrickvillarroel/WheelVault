package io.github.patrickvillarroel.wheel.vault.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@OptIn(AuthUiExperimental::class)
@Composable
fun LoginContent(
    onLoginWithEmailClick: () -> Unit,
    onLoginWithEmailAndPasswordClick: () -> Unit,
    onLoginWithGoogleClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier.fillMaxSize()) { paddingValues ->
        Column(
            Modifier.padding(paddingValues).fillMaxSize().padding(25.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(painterResource(R.drawable.helmet_icon), stringResource(R.string.helmet), Modifier.size(50.dp))
            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(R.string.collectors_project_lines),
                Modifier.padding(bottom = 60.dp),
                style = MaterialTheme.typography.displayLargeEmphasized,
                textAlign = TextAlign.Center,
            )

            Button(
                onRegisterClick,
                Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
            ) {
                Text(
                    stringResource(R.string.register),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            }

            OutlinedButton(onClick = onLoginWithGoogleClick, Modifier.fillMaxWidth()) {
                ProviderButton(Google)
            }

            OutlinedButton(onLoginWithEmailClick, Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Email, "Email")
                Text(
                    stringResource(R.string.continue_with, stringResource(R.string.email)),
                    Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
            }

            OutlinedButton(onLoginWithEmailAndPasswordClick, Modifier.padding(bottom = 50.dp).fillMaxWidth()) {
                Icon(Icons.Filled.Password, stringResource(R.string.password))
                Text(
                    stringResource(R.string.continue_with_email_password),
                    Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
private fun LoginPreview() {
    WheelVaultTheme {
        LoginContent(
            onLoginWithEmailClick = {},
            onLoginWithEmailAndPasswordClick = {},
            onLoginWithGoogleClick = {},
            onRegisterClick = {},
        )
    }
}
