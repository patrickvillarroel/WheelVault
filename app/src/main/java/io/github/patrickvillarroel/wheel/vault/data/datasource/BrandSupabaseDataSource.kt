@file:OptIn(ExperimentalUuidApi::class)

package io.github.patrickvillarroel.wheel.vault.data.datasource

import android.content.Context
import coil3.request.ImageRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.authenticatedStorageItem
import io.github.patrickvillarroel.wheel.vault.data.objects.BrandObj
import io.github.patrickvillarroel.wheel.vault.data.objects.toDomain
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

class BrandSupabaseDataSource(private val supabase: SupabaseClient, private val context: Context) : BrandRepository {

    override suspend fun search(query: String): List<Brand> = supabase.from(BrandObj.TABLE).select {
        filter {
            or {
                BrandObj::name ilike "%$query%"
                BrandObj::description ilike "%$query%"
            }
        }
    }.decodeList<BrandObj>().map { it.toDomain(fetchImage(it.id!!)) }

    override suspend fun fetchAll(): List<Brand> = supabase.from(BrandObj.TABLE).select().decodeList<BrandObj>().map {
        it.toDomain(fetchImage(it.id!!))
    }

    override suspend fun fetch(id: UUID): Brand? = supabase.from(BrandObj.TABLE).select {
        filter { BrandObj::id eq id.toKotlinUuid() }
    }.decodeSingleOrNull<BrandObj>()?.toDomain {
        fetchImage(it.id!!)
    }

    override suspend fun fetchByName(name: String): Brand? = supabase.from(BrandObj.TABLE).select {
        filter { BrandObj::name ilike "%$name%" }
    }.decodeSingleOrNull<BrandObj>()?.toDomain {
        fetchImage(it.id!!)
    }

    override suspend fun fetchByDescription(description: String): Brand? = supabase.from(BrandObj.TABLE).select {
        filter { BrandObj::description ilike "%$description%" }
    }.decodeSingleOrNull<BrandObj>()?.toDomain {
        fetchImage(it.id!!)
    }

    private fun fetchImage(id: Uuid, contentType: String = "png") = ImageRequest.Builder(context)
        .data(authenticatedStorageItem(BrandObj.BUCKET_IMAGES, "$id.$contentType"))
        .build()
}
