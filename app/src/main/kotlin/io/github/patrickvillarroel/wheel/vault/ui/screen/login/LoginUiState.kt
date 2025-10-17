package io.github.patrickvillarroel.wheel.vault.ui.screen.login

sealed interface LoginUiState {
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data object Waiting : LoginUiState
    data class Error(val type: ErrorType, val message: String) : LoginUiState

    enum class ErrorType {
        ALREADY_REGISTER,

        /** Means that the credentials are invalid when login or credentials is leaked when sign up */
        INVALID_CREDENTIALS,
        NETWORK,
        TIMEOUT,
        MISSING_VALIDATION,
        UNKNOWN,
    }
}
