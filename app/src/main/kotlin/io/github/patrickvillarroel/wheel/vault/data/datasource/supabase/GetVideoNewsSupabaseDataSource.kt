package io.github.patrickvillarroel.wheel.vault.data.datasource.supabase

import coil3.request.ImageRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.authenticatedStorageItem
import io.github.patrickvillarroel.wheel.vault.data.objects.VideoObj
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import kotlin.uuid.Uuid
import coil3.PlatformContext as CoilContext

class GetVideoNewsSupabaseDataSource(private val supabase: SupabaseClient, private val context: CoilContext) :
    GetVideosNewsUseCase {
    override suspend fun getVideos(forceRefresh: Boolean): List<VideoNews> = supabase.from(TABLE).select {
        order("created_at", Order.DESCENDING)
    }.decodeList<VideoObj>().map { it.toDomain(buildImageRequest(it.id)) }

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
