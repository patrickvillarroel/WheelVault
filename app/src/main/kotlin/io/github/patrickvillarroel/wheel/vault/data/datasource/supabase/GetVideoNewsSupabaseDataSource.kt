package io.github.patrickvillarroel.wheel.vault.data.datasource.supabase

import coil3.request.ImageRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.authenticatedStorageItem
import io.github.patrickvillarroel.wheel.vault.data.objects.VideoObj
import io.github.patrickvillarroel.wheel.vault.domain.model.Page
import io.github.patrickvillarroel.wheel.vault.domain.model.PagedSource
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import kotlin.uuid.Uuid
import coil3.PlatformContext as CoilContext

class GetVideoNewsSupabaseDataSource(private val supabase: SupabaseClient, private val context: CoilContext) :
    GetVideosNewsUseCase {
    override suspend fun getVideos(forceRefresh: Boolean): List<VideoNews> = supabase.from(TABLE).select {
        order("created_at", Order.DESCENDING)
    }.decodeList<VideoObj>().map { it.toDomain(buildImageRequest(it.id)) }

    override fun getVideosPaged(): PagedSource<Int, VideoNews> = PagedSource { key, size ->
        val offset = key ?: 0

        val data = supabase.from(TABLE).select {
            order("created_at", Order.DESCENDING)
            limit(size.toLong())
            range(offset.toLong(), (offset + size - 1).toLong())
        }.decodeList<VideoObj>().map { v -> v.toDomain(buildImageRequest(v.id)) }

        val nextKey = if (data.size < size) null else offset + size
        val prevKey = if (offset == 0) null else maxOf(offset - size, 0)

        Page(data = data, prevKey = prevKey, nextKey = nextKey)
    }

    private fun buildImageRequest(id: Uuid, contentType: String = "png") = ImageRequest.Builder(context)
        .data(buildStorageItem(id, contentType))
        .build()

    fun buildStorageItem(id: Uuid, contentType: String = "png") = authenticatedStorageItem(BUCKET, "$id.$contentType")

    companion object {
        private const val TABLE = "news"
        private const val BUCKET = "news-thumbnails"

        private fun VideoObj.toDomain(thumbnail: Any) = VideoNews(
            id = this.id,
            name = this.name,
            link = this.link,
            thumbnail = thumbnail,
            description = this.description,
            createdAt = this.createdAt,
        )
    }
}
