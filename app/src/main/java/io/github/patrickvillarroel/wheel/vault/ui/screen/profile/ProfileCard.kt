package io.github.patrickvillarroel.wheel.vault.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun ProfileCard(
    email: String,
    isEditable: Boolean,
    onEditClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    linkedAccounts: Map<AuthProvider, Boolean>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A3540)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .background(Color(0xFFDF2C2C))
                    .fillMaxWidth(.5f)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    "Collector",
                    Modifier.fillMaxWidth(),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Spacer(Modifier.height(25.dp))
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                readOnly = !isEditable,
                label = { Text("Correo") },
                trailingIcon = {
                    IconButton(onClick = onEditClick, enabled = isEditable) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar correo")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.LightGray,
                    focusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF2C2C2E),
                    focusedBorderColor = Color.Red,
                    disabledBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    unfocusedLabelColor = Color.LightGray,
                    focusedLabelColor = Color.White,
                ),
            )

            Spacer(Modifier.height(16.dp))

            Text("Cuentas vinculadas", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AuthProvider.entries.forEach { provider ->
                    val isLinked = linkedAccounts[provider] == true
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isLinked) Color.White else Color.Gray.copy(alpha = 0.2f)),
                    ) {
                        if (isLinked) {
                            provider.Icon(
                                modifier = Modifier.align(Alignment.Center),
                                tint = Color.Unspecified,
                            )
                        } else {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray.copy(alpha = 0.6f), CircleShape),
                            ) {
                                provider.Icon(
                                    tint = Color.Black,
                                    modifier = Modifier.align(Alignment.Center).size(30.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CardPreview() {
    WheelVaultTheme {
        ProfileCard(
            email = "james.c.mcreynolds@example-pet-store.com",
            isEditable = false,
            onEditClick = {},
            onEmailChange = {},
            linkedAccounts = mapOf(
                AuthProvider.Email to true,
                AuthProvider.Password to false,
                AuthProvider.Google to true,
            ),
        )
    }
}
