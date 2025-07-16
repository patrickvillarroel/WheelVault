package io.github.patrickvillarroel.wheel.vault.ui.screen.profile

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.session.SessionViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    backCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
    sessionViewModel: SessionViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val currentUser by sessionViewModel.currentUser.collectAsState()
    var email by rememberSaveable(currentUser) { mutableStateOf(currentUser?.email ?: "") }
    var linkedProviders = remember(currentUser) { mutableMapOf<AuthProvider, Boolean>() }

    LaunchedEffect(currentUser) {
        currentUser?.identities?.forEach { identity ->
            val providers = mutableMapOf<AuthProvider, Boolean>()
            if (identity.provider == "google") {
                providers[AuthProvider.Google] = true
            }
            if (identity.provider == "email") {
                providers[AuthProvider.Email] = true
                providers[AuthProvider.Password] = true
            }
            linkedProviders = providers
        }
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
            onProviderClick = {
                Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
            },
            onLogout = sessionViewModel::logout,
            backCallbacks = backCallbacks,
        ),
        modifier = modifier,
    )
}
