package io.github.patrickvillarroel.wheel.vault.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import io.github.patrickvillarroel.wheel.vault.domain.usecase.UpdateOnboardingStateUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest

data class UpdateOnboardingStateUseCaseImpl(private val dataStore: DataStore<Preferences>) :
    UpdateOnboardingStateUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getOnboardingState(): Boolean = dataStore.data.mapLatest {
        it[ONBOARDING_STATE] ?: false
    }.first()

    override suspend fun updateOnboardingState(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_STATE] = completed
        }
    }

    companion object {
        val Context.dataStore by preferencesDataStore(name = "dado")
        private val ONBOARDING_STATE = booleanPreferencesKey("onboarding_completed")
    }
}
