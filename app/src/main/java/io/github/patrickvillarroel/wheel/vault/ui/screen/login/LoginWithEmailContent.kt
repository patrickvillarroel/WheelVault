package io.github.patrickvillarroel.wheel.vault.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.compose.auth.ui.AuthForm
import io.github.jan.supabase.compose.auth.ui.LocalAuthState
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@OptIn(AuthUiExperimental::class)
@Composable
fun LoginWithEmailContent(
    onClick: (String, String) -> Unit,
    isRegister: Boolean,
    isMagicLink: Boolean,
    modifier: Modifier = Modifier,
) {
    val displayLabel = if (isRegister) {
        "Registrate"
    } else if (isMagicLink) {
        "Ingresar con Correo"
    } else {
        "Iniciar Sesión"
    }
    var email by rememberSaveable(isRegister, isMagicLink) { mutableStateOf("") }
    var password by rememberSaveable(isRegister, isMagicLink) { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF1D1B20),
    ) { paddingValues ->
        Box(
            Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            Text(
                stringResource(R.string.collectors_project_lines),
                style = MaterialTheme.typography.displayLargeEmphasized,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp),
                color = Color.White,
            )

            Text(
                displayLabel,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .offset(y = 170.dp),
            )

            Box(
                Modifier
                    .width(268.dp)
                    .height(265.dp)
                    .align(Alignment.Center),
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp)
                        .background(Color(0xFFE53935), RoundedCornerShape(size = 20.dp)),
                ) {
                    AuthForm {
                        val state = LocalAuthState.current
                        Column(Modifier.padding(10.dp).fillMaxSize(), verticalArrangement = Arrangement.Center) {
                            OutlinedEmailField(
                                email,
                                onValueChange = { email = it },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black,
                                ),
                                label = { Text("Correo electrónico", color = Color.White) },
                            )

                            if (!isMagicLink) {
                                OutlinedPasswordField(
                                    password,
                                    onValueChange = { password = it },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Black,
                                        unfocusedBorderColor = Color.Black,
                                    ),
                                    label = { Text("Contraseña", color = Color.White) },
                                )
                            }

                            Button(
                                onClick = { onClick(email, password) },
                                enabled = state.validForm,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    disabledContainerColor = Color.Gray,
                                ),
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                            ) {
                                Text("Ingresar", style = MaterialTheme.typography.labelLarge, color = Color.White)
                            }
                        }
                    }
                }

                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-30).dp),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.helmet_icon),
                        contentDescription = "Helmet Icon",
                        modifier = Modifier
                            .size(60.dp)
                            .weight(1f)
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginPreview() {
    WheelVaultTheme {
        LoginWithEmailContent(onClick = { _, _ -> }, isRegister = false, isMagicLink = true)
    }
}
