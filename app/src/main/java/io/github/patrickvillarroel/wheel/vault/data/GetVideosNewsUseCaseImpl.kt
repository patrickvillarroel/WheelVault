package io.github.patrickvillarroel.wheel.vault.data

import android.content.Context
import coil3.request.ImageRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.authenticatedStorageItem
import io.github.patrickvillarroel.wheel.vault.data.objects.VideoObj
import io.github.patrickvillarroel.wheel.vault.data.objects.toDomain
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetVideosNewsUseCaseImpl(private val supabase: SupabaseClient, private val context: Context) :
    GetVideosNewsUseCase {
    override suspend fun getVideos(): List<VideoNews> = supabase.from(VideoObj.TABLE).select {
        order("created_at", Order.DESCENDING)
    }.decodeList<VideoObj>().map { it.toDomain(fetchImage(it.id)) }

    private fun fetchImage(id: Uuid, contentType: String = "png") = ImageRequest.Builder(context)
        .data(authenticatedStorageItem(VideoObj.BUCKET, "$id.$contentType"))
        .build()
}
