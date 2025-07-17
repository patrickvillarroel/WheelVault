package io.github.patrickvillarroel.wheel.vault.ui.screen.profile

import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks

data class ProfileCallbacks(
    val onEditClick: () -> Unit,
    val onEmailChange: (String) -> Unit,
    val linkedAccounts: Map<AuthProvider, Boolean>,
    val onLogout: () -> Unit,
    val backCallbacks: HeaderBackCallbacks,
)
