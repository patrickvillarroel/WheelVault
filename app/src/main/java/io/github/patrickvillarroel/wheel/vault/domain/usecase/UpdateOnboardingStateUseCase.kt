package io.github.patrickvillarroel.wheel.vault.domain.usecase

interface UpdateOnboardingStateUseCase {
    suspend fun getOnboardingState(): Boolean
    suspend fun updateOnboardingState(completed: Boolean)
}
