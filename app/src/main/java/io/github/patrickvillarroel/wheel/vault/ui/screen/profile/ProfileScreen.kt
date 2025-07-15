package io.github.patrickvillarroel.wheel.vault.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks

@Composable
fun ProfileScreen(backCallbacks: HeaderBackCallbacks, modifier: Modifier = Modifier) {
    // TODO connect with supabase and VM
    ProfileContent(
        email = "james.c.mcreynolds@example-pet-store.com",
        isEditable = false,
        callbacks = ProfileCallbacks(
            onEditClick = {},
            onEmailChange = {},
            linkedAccounts = mapOf(
                AuthProvider.Email to true,
                AuthProvider.Password to true,
                AuthProvider.Google to true,
            ),
            onProviderClick = {},
            onLogout = {},
            backCallbacks = backCallbacks,
        ),
        modifier = modifier,
    )
}
