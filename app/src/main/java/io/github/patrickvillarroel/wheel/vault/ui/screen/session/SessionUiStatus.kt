package io.github.patrickvillarroel.wheel.vault.ui.screen.session

import io.github.jan.supabase.auth.user.UserSession

sealed interface SessionUiStatus {
    object Initializing : SessionUiStatus
    object NotAuthenticated : SessionUiStatus
    data class Authenticated(val session: UserSession) : SessionUiStatus
    object RefreshFailure : SessionUiStatus
}
