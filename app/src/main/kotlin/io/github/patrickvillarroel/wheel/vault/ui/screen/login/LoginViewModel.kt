package io.github.patrickvillarroel.wheel.vault.ui.screen.login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.exception.AuthWeakPasswordException
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.patrickvillarroel.wheel.vault.BuildConfig
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import io.github.jan.supabase.auth.providers.builtin.Email as ProviderEmail

class LoginViewModel(private val supabase: SupabaseClient) : ViewModel() {
    companion object {
        private val logger = Logger.withTag("LoginViewModel")
    }
    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Waiting)
    val state = _state.asStateFlow()

    /** Register a new user with email and password */
    fun register(email: String, password: String) {
        viewModelScope.launch {
            try {
                _state.update { LoginUiState.Loading }
                supabase.auth.signUpWith(ProviderEmail) {
                    this.email = email
                    this.password = password
                }
                _state.update { LoginUiState.Success }
            } catch (e: AuthWeakPasswordException) {
                logger.e("Register with email and password fail with AuthWeakPasswordException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.INVALID_CREDENTIALS, "Password is too weak") }
            } catch (e: AuthRestException) {
                logger.e("AuthRestException: ${e.errorCode?.value ?: e.errorDescription}", e)
                _state.update {
                    LoginUiState.Error(
                        LoginUiState.ErrorType.INVALID_CREDENTIALS,
                        e.errorCode?.value ?: e.errorDescription,
                    )
                }
            } catch (e: HttpRequestException) {
                logger.e("Register with email and password fail with HttpRequestException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.NETWORK, "Network") }
            } catch (e: HttpRequestTimeoutException) {
                logger.e("Register with email and password fail with HttpRequestTimeoutException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.TIMEOUT, "Timeout") }
            } catch (e: RestException) {
                logger.e("Register with email and password fail with RestException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.UNKNOWN, "Unknown error") }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Register with email and password fail with Unhandled Exception", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.UNKNOWN, "Unknown error") }
            }
        }
    }

    /** Login a user with email and password */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _state.update { LoginUiState.Loading }
                supabase.auth.signInWith(ProviderEmail) {
                    this.email = email
                    this.password = password
                }
                _state.update { LoginUiState.Success }
            } catch (e: AuthRestException) {
                logger.e("Login with email and password fail with AuthRestException: ${e.errorCode?.value ?: e.errorDescription}", e)
                _state.update {
                    LoginUiState.Error(
                        LoginUiState.ErrorType.INVALID_CREDENTIALS,
                        e.errorCode?.value ?: e.errorDescription,
                    )
                }
            } catch (e: HttpRequestException) {
                logger.e("Login with email and password fail with HttpRequestException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.NETWORK, "Network") }
            } catch (e: HttpRequestTimeoutException) {
                logger.e("Login with email and password fail with HttpRequestTimeoutException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.TIMEOUT, "Timeout") }
            } catch (e: RestException) {
                logger.e("Login with email and password fail with RestException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.UNKNOWN, "Unknown error") }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Login with email and password fail with Unhandled Exception", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.UNKNOWN, "Unknown error") }
            }
        }
    }

    /** Register with Google flow */
    @OptIn(ExperimentalUuidApi::class)
    fun login(context: Context) {
        viewModelScope.launch {
            try {
                _state.update { LoginUiState.Loading }
                val credentialManager = CredentialManager.create(context)

                val rawNonce = Uuid.random().toString()
                val bytes = rawNonce.toByteArray()
                val md: MessageDigest = MessageDigest.getInstance("SHA-256")
                val digest: ByteArray = md.digest(bytes)
                val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.SUPABASE_WEB_KEY)
                    .setNonce(hashedNonce)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val response = credentialManager.getCredential(context, request)
                val googleIdToken = GoogleIdTokenCredential.createFrom(response.credential.data).idToken

                supabase.auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                    nonce = rawNonce
                }

                _state.update { LoginUiState.Success }
            } catch (e: GetCredentialException) {
                logger.e("Login with Google fail with GetCredentialException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.INVALID_CREDENTIALS, "Problems with Google. ${e.message}") }
            } catch (e: HttpRequestException) {
                logger.e("Login with Google fail with HttpRequestException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.NETWORK, "Network") }
            } catch (e: HttpRequestTimeoutException) {
                logger.e("Login with Google fail with HttpRequestTimeoutException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.TIMEOUT, "Timeout") }
            } catch (e: RestException) {
                logger.e("Login with Google fail with RestException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.UNKNOWN, "Unknown error") }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Login with Google fail with Unhandled Exception", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.UNKNOWN, "Unknown error") }
            }
        }
    }

    /** Send a magic link and await the deep link triggered and supabase handle it */
    fun login(email: String) {
        viewModelScope.launch {
            try {
                supabase.auth.signInWith(OTP, "https://wheel.supabase.com/magic-login-callback") {
                    this.email = email
                }
            } catch (e: HttpRequestException) {
                logger.e("Login with email magic link fail with HttpRequestException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.NETWORK, "Network") }
            } catch (e: HttpRequestTimeoutException) {
                logger.e("Login with email magic link fail with HttpRequestTimeoutException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.TIMEOUT, "Timeout") }
            } catch (e: RestException) {
                logger.e("Login with email magic link fail with RestException", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.UNKNOWN, "Unknown error") }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Login with email magic link fail with Unhandled Exception", e)
                _state.update { LoginUiState.Error(LoginUiState.ErrorType.UNKNOWN, "Unknown error") }
            }
        }
    }

    fun resetState() {
        _state.update { LoginUiState.Waiting }
    }
}
