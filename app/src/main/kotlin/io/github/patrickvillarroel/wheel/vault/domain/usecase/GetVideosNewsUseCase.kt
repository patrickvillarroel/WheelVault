package io.github.patrickvillarroel.wheel.vault.domain.usecase

import io.github.patrickvillarroel.wheel.vault.domain.model.PagedSource
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews

interface GetVideosNewsUseCase {
    suspend fun getVideos(forceRefresh: Boolean = false): List<VideoNews>
    fun getVideosPaged(): PagedSource<Int, VideoNews>
}
