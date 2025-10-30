package io.github.patrickvillarroel.wheel.vault.data.datasource.supabase

import android.content.Context
import coil3.request.ImageRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.authenticatedStorageItem
import io.github.patrickvillarroel.wheel.vault.data.objects.BrandObj
import io.github.patrickvillarroel.wheel.vault.data.objects.toDomain
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import kotlin.uuid.Uuid

class BrandSupabaseDataSource(private val supabase: SupabaseClient, private val context: Context) : BrandRepository {

    override suspend fun search(query: String): List<Brand> = supabase.from(BrandObj.TABLE).select {
        filter {
            or {
                ilike("name", "%$query%")
                ilike("description", "%$query%")
            }
        }
    }.decodeList<BrandObj>().map { it.toDomain(fetchImage(it.id!!)) }

    override suspend fun fetchAll(forceRefresh: Boolean): List<Brand> =
        supabase.from(BrandObj.TABLE).select().decodeList<BrandObj>().map {
            it.toDomain(fetchImage(it.id!!))
        }

    override suspend fun fetch(id: Uuid): Brand? = supabase.from(BrandObj.TABLE).select {
        filter { eq("id", id) }
    }.decodeSingleOrNull<BrandObj>()?.toDomain {
        fetchImage(it.id!!)
    }

    override suspend fun fetchByName(name: String): Brand? = supabase.from(BrandObj.TABLE).select {
        filter { ilike("name", "%$name%") }
    }.decodeSingleOrNull<BrandObj>()?.toDomain {
        fetchImage(it.id!!)
    }

    override suspend fun fetchByDescription(description: String): Brand? = supabase.from(BrandObj.TABLE).select {
        filter { ilike("description", "%$description%") }
    }.decodeSingleOrNull<BrandObj>()?.toDomain {
        fetchImage(it.id!!)
    }

    private fun fetchImage(id: Uuid, contentType: String = "png") = ImageRequest.Builder(context)
        .data(authenticatedStorageItem(BrandObj.BUCKET_IMAGES, "$id.$contentType"))
        .build()

    fun buildImageRequest(id: Uuid, contentType: String = "png") =
        authenticatedStorageItem(BrandObj.BUCKET_IMAGES, "$id.$contentType")
}
