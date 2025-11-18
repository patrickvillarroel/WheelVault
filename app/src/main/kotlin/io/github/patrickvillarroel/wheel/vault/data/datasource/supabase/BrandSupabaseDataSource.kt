package io.github.patrickvillarroel.wheel.vault.data.datasource.supabase

import coil3.request.ImageRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.authenticatedStorageItem
import io.github.patrickvillarroel.wheel.vault.data.objects.BrandObj
import io.github.patrickvillarroel.wheel.vault.data.objects.toDomain
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.Page
import io.github.patrickvillarroel.wheel.vault.domain.model.PagedSource
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import kotlin.uuid.Uuid
import coil3.PlatformContext as CoilContext

class BrandSupabaseDataSource(private val supabase: SupabaseClient, private val context: CoilContext) :
    BrandRepository {

    override suspend fun search(query: String): List<Brand> = supabase.from(BrandObj.TABLE).select {
        filter {
            or {
                ilike("name", "%$query%")
                ilike("description", "%$query%")
            }
        }
    }.decodeList<BrandObj>().map { it.toDomain(fetchImage(it.id!!)) }

    override suspend fun fetchAllNames(forceRefresh: Boolean): List<String> =
        supabase.from(BrandObj.TABLE).select(Columns.list("name")) {
            order("created_at", Order.DESCENDING)
        }.decodeList<Map<String, String>>().flatMap { it.values }

    override suspend fun fetchAllImages(forceRefresh: Boolean): Map<Uuid, Any> =
        supabase.from(BrandObj.TABLE).select(Columns.list("id")) {
            order("created_at", Order.DESCENDING)
        }.decodeList<Map<String, Uuid>>()
            .flatMap { it.values }
            .associateWith { fetchImage(it) }

    override fun fetchAllImagesPaged(): PagedSource<Int, Pair<Uuid, Any>> = PagedSource { key, size ->
        val offset = key ?: 0

        val data = supabase.from(BrandObj.TABLE).select(Columns.list("id")) {
            order("created_at", Order.DESCENDING)
            limit(size.toLong())
            range(offset.toLong(), (offset + size - 1).toLong())
        }.decodeList<Map<String, Uuid>>()
            .flatMap { it.values }
            .associateWith { fetchImage(it) }
            .toList()

        val nextKey = if (data.size < size) null else offset + size
        val prevKey = if (offset == 0) null else maxOf(offset - size, 0)

        Page(data = data, prevKey = prevKey, nextKey = nextKey)
    }

    override suspend fun fetchAll(forceRefresh: Boolean): List<Brand> = supabase.from(BrandObj.TABLE).select {
        order("created_at", Order.DESCENDING)
    }.decodeList<BrandObj>().map { it.toDomain(fetchImage(it.id!!)) }

    override suspend fun fetch(id: Uuid, forceRefresh: Boolean): Brand? = supabase.from(BrandObj.TABLE).select {
        filter { eq("id", id) }
    }.decodeSingleOrNull<BrandObj>()?.toDomain { fetchImage(it.id!!) }

    override suspend fun fetchByName(name: String): Brand? = supabase.from(BrandObj.TABLE).select {
        filter { ilike("name", "%$name%") }
    }.decodeSingleOrNull<BrandObj>()?.toDomain { fetchImage(it.id!!) }

    override suspend fun fetchByDescription(description: String): Brand? = supabase.from(BrandObj.TABLE).select {
        filter { ilike("description", "%$description%") }
    }.decodeSingleOrNull<BrandObj>()?.toDomain { fetchImage(it.id!!) }

    private fun fetchImage(id: Uuid, contentType: String = "png") = ImageRequest.Builder(context)
        .data(buildImageRequest(id, contentType))
        .build()

    fun buildImageRequest(id: Uuid, contentType: String = "png") =
        authenticatedStorageItem(BrandObj.BUCKET_IMAGES, "$id.$contentType")
}
