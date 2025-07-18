package io.github.patrickvillarroel.wheel.vault.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patrickvillarroel.wheel.vault.domain.usecase.UpdateOnboardingStateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingViewModel(private val useCase: UpdateOnboardingStateUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                if (useCase.getOnboardingState()) OnboardingUiState.Success else OnboardingUiState.Uncompleted
            }
        }
    }

    fun updateOnboardingState() {
        viewModelScope.launch {
            useCase.updateOnboardingState(true)
            _uiState.update { OnboardingUiState.Success }
        }
    }

    sealed interface OnboardingUiState {
        data object Loading : OnboardingUiState
        data object Success : OnboardingUiState
        data object Uncompleted : OnboardingUiState
    }
}
