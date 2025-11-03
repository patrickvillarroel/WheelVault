package io.github.patrickvillarroel.wheel.vault.ui.screen.session

import io.github.jan.supabase.auth.user.UserSession

sealed interface SessionUiStatus {
    data object Initializing : SessionUiStatus
    data object NotAuthenticated : SessionUiStatus
    data class Authenticated(val session: UserSession) : SessionUiStatus
    data object RefreshFailure : SessionUiStatus
}
