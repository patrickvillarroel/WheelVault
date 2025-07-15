package io.github.patrickvillarroel.wheel.vault.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onGarageClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO connect with supabase and VM
    ProfileContent(
        email = "james.c.mcreynolds@example-pet-store.com",
        isEditable = false,
        onEditClick = {},
        onEmailChange = {},
        linkedAccounts = mapOf(
            AuthProvider.Email to true,
            AuthProvider.Password to true,
            AuthProvider.Google to true,
        ),
        onProviderClick = {},
        onBackClick = onBackClick,
        onGarageClick = onGarageClick,
        onFavoritesClick = onFavoritesClick,
        onStatisticsClick = onStatisticsClick,
        onLogout = {},
        modifier = modifier,
    )
}
