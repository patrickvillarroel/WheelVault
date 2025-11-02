package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getVideosNewsUseCase: GetVideosNewsUseCase,
    private val brandRepository: BrandRepository,
) : ViewModel() {
    private val _brandsState = MutableStateFlow<BrandsUiState>(BrandsUiState.Loading)
    val brandsState = _brandsState.asStateFlow()

    private val _news = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val news = _news
        .filterIsInstance<NewsUiState.Success>()
        .map { it.news }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val brandsImages = brandsState.map { state ->
        if (state is BrandsUiState.Success) {
            state.brands.map { it.id to it.image }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun fetchAllBrands(force: Boolean = false) {
        _brandsState.update { BrandsUiState.Loading }
        logger.d { "Fetching all brands" }
        viewModelScope.launch {
            try {
                val result = brandRepository.fetchAll(force)
                _brandsState.update { BrandsUiState.Success(result) }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Error fetch all", e)
                _brandsState.update { BrandsUiState.Error }
            }
        }
    }

    fun fetchNews(force: Boolean = false) {
        // Only this needs a manual force decision because don't have a room table to local persist
        val snapshotState = _news.value
        if (!force && snapshotState is NewsUiState.Success && snapshotState.news.size > 1) {
            logger.d { "The current state of news is success and have ${snapshotState.news.size} news" }
            return
        }
        _news.update { NewsUiState.Loading }
        logger.i("Fetching news...")
        viewModelScope.launch {
            val videos = getVideosNewsUseCase.getVideos()
            logger.i { "Fetched ${videos.size} news" }
            val status = NewsUiState.Success(videos)
            _news.update { status }
        }
    }

    companion object {
        private val logger = Logger.withTag("HomeViewModel")
    }

    sealed interface BrandsUiState {
        data object Loading : BrandsUiState

        @Immutable
        data class Success(@Stable val brands: List<Brand>) : BrandsUiState
        data object Error : BrandsUiState
    }

    sealed interface NewsUiState {
        object Loading : NewsUiState

        @Immutable
        class Success(@Stable val news: List<VideoNews>) : NewsUiState
    }
}
