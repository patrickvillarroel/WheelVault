package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class HomeViewModel(
    private val getVideosNewsUseCase: GetVideosNewsUseCase,
    private val brandRepository: BrandRepository,
    private val carsRepository: CarsRepository,
) : ViewModel() {
    companion object {
        private val logger = Logger.withTag("HomeViewModel")
    }
    private val _news = MutableStateFlow(emptyList<VideoNews>())

    val news = _news.asStateFlow()

    private val _brandsImages = MutableStateFlow(emptyMap<Uuid, Any>())

    val brandsImages = _brandsImages.asStateFlow()
    private val _recentCarImages = MutableStateFlow<Map<Uuid, Any>>(emptyMap())

    val recentCarImages = _recentCarImages.asStateFlow()

    fun fetchAllBrands(force: Boolean = false) {
        logger.v { "Fetching all brands images" }
        viewModelScope.launch {
            try {
                val result = brandRepository.fetchAllImages(force)
                logger.d { "Fetched ${result.size} brands images" }
                _brandsImages.update { result }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Error fetching all images of brands", e)
                _brandsImages.update { emptyMap() }
            }
        }
    }

    fun fetchAllCarImages(force: Boolean = false) {
        val snapshotState = _recentCarImages.value
        if (!force && snapshotState.size > 1) {
            logger.d { "Skipping loading car images for home" }
            return
        }

        logger.v { "Fetching all car images" }
        viewModelScope.launch {
            try {
                // TODO paginar
                // TODO use force param when SyncMediator is implemented
                val result = carsRepository.fetchAllImage(orderAsc = false)
                logger.d { "Fetched ${result.size} car images" }
                _recentCarImages.update { result }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e(e) { "Error loading car images for home" }
                _recentCarImages.update { emptyMap() }
            }
        }
    }

    fun fetchNews(force: Boolean = false) {
        logger.v("Fetching news...")
        viewModelScope.launch {
            try {
                val videos = getVideosNewsUseCase.getVideos(force)
                logger.d { "Fetched ${videos.size} news" }
                _news.update { videos }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e(e) { "Error loading news for home" }
                _news.update { emptyList() }
            }
        }
    }
}
