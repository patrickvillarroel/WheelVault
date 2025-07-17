package io.github.patrickvillarroel.wheel.vault.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BrandViewModel(private val brandRepository: BrandRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<BrandUiState>(BrandUiState.Loading)
    val uiState = _uiState.asStateFlow()
    val brandsImages = _uiState.map { state ->
        if (state is BrandUiState.SuccessList) {
            state.brands.map { it.id to it.image }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val brandsNames = _uiState.map { state ->
        if (state is BrandUiState.SuccessList) {
            state.brands.map { it.name }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun fetchAll() {
        if (brandsImages.value.isEmpty() ||
            _uiState.value is BrandUiState.Error ||
            _uiState.value is BrandUiState.Loading
        ) {
            viewModelScope.launch {
                Log.i("Brands", "Fetching all brands")
                _uiState.update { BrandUiState.Loading }
                try {
                    val result = brandRepository.fetchAll()
                    _uiState.update { BrandUiState.SuccessList(result) }
                } catch (e: Exception) {
                    Log.i("Brands", "Error fetch all", e)
                    _uiState.update { BrandUiState.Error }
                }
            }
        }
    }

    sealed interface BrandUiState {
        object Loading : BrandUiState

        @androidx.compose.runtime.Immutable
        data class SuccessList(@androidx.compose.runtime.Stable val brands: List<Brand>) : BrandUiState
        data object Error : BrandUiState
    }
}
