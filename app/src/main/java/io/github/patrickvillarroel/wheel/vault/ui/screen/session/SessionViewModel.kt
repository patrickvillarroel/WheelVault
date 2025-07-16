package io.github.patrickvillarroel.wheel.vault.ui.screen.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SessionViewModel(private val supabase: SupabaseClient) : ViewModel() {
    val session = supabase.auth.sessionStatus.map {
        when (it) {
            SessionStatus.Initializing -> SessionUiStatus.Initializing
            is SessionStatus.Authenticated -> SessionUiStatus.Authenticated(it.session)
            is SessionStatus.NotAuthenticated -> SessionUiStatus.NotAuthenticated
            is SessionStatus.RefreshFailure -> SessionUiStatus.RefreshFailure
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SessionUiStatus.Initializing)

    fun currentSessionOrNull() = supabase.auth.currentSessionOrNull()
}
