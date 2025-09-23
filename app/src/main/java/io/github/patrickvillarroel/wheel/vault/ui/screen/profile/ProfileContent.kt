package io.github.patrickvillarroel.wheel.vault.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BackTextButton
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuHeader
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun ProfileContent(email: String, isEditable: Boolean, callbacks: ProfileCallbacks, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MenuHeader(callbacks.backCallbacks) {
                BackTextButton(callbacks.backCallbacks.onBackClick)
            }
        },
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {
            ProfileCard(
                email = email,
                isEditable = isEditable,
                onEditClick = callbacks.onEditClick,
                onEmailChange = callbacks.onEmailChange,
                linkedAccounts = callbacks.linkedAccounts,
                modifier = Modifier.align(Alignment.TopCenter).offset(y = 70.dp),
            )

            Row(
                Modifier.align(Alignment.TopCenter).fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(
                    painterResource(R.drawable.helmet_icon),
                    stringResource(R.string.helmet),
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(5.dp, Color.Black, CircleShape)
                        .padding(8.dp),
                )
                Spacer(Modifier.width(30.dp))
                Text(
                    stringResource(R.string.user),
                    Modifier.align(Alignment.CenterVertically).offset(y = (-20).dp),
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }

            Button(
                onClick = callbacks.onLogout,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 70.dp)
                    .fillMaxWidth(0.6f),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDF2C2C)),
            ) {
                Text(stringResource(R.string.logout), color = Color.White)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ProfileContentPreview() {
    WheelVaultTheme {
        ProfileContent(
            email = "james.c.mcreynolds@example-pet-store.com",
            isEditable = false,
            callbacks = ProfileCallbacks(
                onEditClick = {},
                onEmailChange = {},
                linkedAccounts = mapOf(AuthProvider.Email to true, AuthProvider.Password to false),
                backCallbacks = HeaderBackCallbacks.default,
                onLogout = {},
            ),
        )
    }
}
