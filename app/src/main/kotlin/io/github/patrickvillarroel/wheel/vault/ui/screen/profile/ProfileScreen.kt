package io.github.patrickvillarroel.wheel.vault.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.session.SessionViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    backCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
    sessionViewModel: SessionViewModel = koinViewModel(),
) {
    val currentUser by sessionViewModel.currentUser.collectAsState()
    var email by rememberSaveable(currentUser) { mutableStateOf(currentUser?.email ?: "") }
    val linkedProviders = remember(currentUser) {
        currentUser?.identities?.flatMap { identity ->
            when (identity.provider.lowercase()) {
                "google" -> listOf(AuthProvider.Google to true)
                "email" -> listOf(
                    AuthProvider.Email to true,
                    AuthProvider.Password to true,
                )
                else -> emptyList()
            }
        }?.toMap() ?: emptyMap()
    }

    ProfileContent(
        email = email,
        isEditable = linkedProviders.contains(AuthProvider.Email),
        callbacks = ProfileCallbacks(
            onEditClick = { sessionViewModel.updateEmail(email) },
            onEmailChange = {
                email = it
            },
            linkedAccounts = linkedProviders,
            onLogout = sessionViewModel::logout,
            backCallbacks = backCallbacks,
        ),
        modifier = modifier,
    )
}
