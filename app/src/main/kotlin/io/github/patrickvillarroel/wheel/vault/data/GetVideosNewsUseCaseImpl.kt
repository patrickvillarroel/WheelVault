package io.github.patrickvillarroel.wheel.vault.data

import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageDownloadHelper
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.GetVideoNewsRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.GetVideoNewsSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.model.PagedSource
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import kotlinx.coroutines.launch

class GetVideosNewsUseCaseImpl(
    private val room: GetVideoNewsRoomDataSource,
    private val supabase: GetVideoNewsSupabaseDataSource,
    private val imageDownloadHelper: ImageDownloadHelper,
) : GetVideosNewsUseCase {
    override suspend fun getVideos(forceRefresh: Boolean): List<VideoNews> = SyncMediator.fetchList(
        forceRefresh = forceRefresh,
        localFetch = { room.getVideos(forceRefresh) },
        remoteFetch = { supabase.getVideos(forceRefresh) },
        saveRemote = { videos ->
            launch {
                val images = videos.associate { videoNews ->
                    videoNews.id to imageDownloadHelper.downloadImage(supabase.buildStorageItem(videoNews.id))
                }.filterValuesNotNull()
                room.save(videos, images)
            }
        },
    )

    // TODO connect with ROOM
    override fun getVideosPaged(): PagedSource<Int, VideoNews> = supabase.getVideosPaged()

    @Suppress("UNCHECKED_CAST")
    private fun <K, V> Map<K, V?>.filterValuesNotNull(): Map<K, V> = this.filterValues { value ->
        value != null
    } as Map<K, V>
}
