package io.github.patrickvillarroel.wheel.vault.ui.screen.profile

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks

@Immutable
data class ProfileCallbacks(
    val onEditClick: () -> Unit,
    val onEmailChange: (String) -> Unit,
    @Stable val linkedAccounts: Map<AuthProvider, Boolean>,
    val onLogout: () -> Unit,
    val backCallbacks: HeaderBackCallbacks,
)
