package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeViewModel(private val getVideosNewsUseCase: GetVideosNewsUseCase) : ViewModel() {
    private val newsUiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val news = newsUiState.map {
        (it as? NewsUiState.Success)?.news ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch {
            Log.i("HomeViewModel", "Fetching news...")
            val videos = getVideosNewsUseCase.getVideos()
            Log.i("HomeViewModel", "Fetched news: $videos")
            newsUiState.update { NewsUiState.Success(videos) }
        }
    }

    sealed interface NewsUiState {
        data object Loading : NewsUiState

        @Immutable
        data class Success(@Stable val news: List<VideoNews>) : NewsUiState
    }
}
