@file:OptIn(ExperimentalUuidApi::class)

package io.github.patrickvillarroel.wheel.vault.data.datasource

import android.content.Context
import android.util.Log
import coil3.request.ImageRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.authenticatedStorageItem
import io.github.jan.supabase.storage.storage
import io.github.patrickvillarroel.wheel.vault.data.objects.CarObj
import io.github.patrickvillarroel.wheel.vault.data.objects.toDomain
import io.github.patrickvillarroel.wheel.vault.data.objects.toObject
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.UUID
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class CarSupabaseDataSource(private val supabase: SupabaseClient, private val context: Context) : CarsRepository {
    override suspend fun search(query: String, isFavorite: Boolean): List<CarItem> {
        TODO()
    }

    override suspend fun fetchAll(isFavorite: Boolean, limit: Int): List<CarItem> = supabase.from(CarObj.TABLE).select {
        filter {
            eq("user_id", supabase.auth.currentUserOrNull()!!.id)
            if (isFavorite) {
                CarObj::isFavorite eq true
            }
        }
        limit(limit.toLong())
        order("created_at", Order.DESCENDING)
    }.decodeList<CarObj>().map { it.toDomain(fetchAllImages(it.id!!)) }

    override suspend fun fetch(id: UUID): CarItem? {
        TODO()
    }

    override suspend fun fetchByModel(model: String, isFavorite: Boolean): CarItem? {
        TODO()
    }

    override suspend fun fetchByYear(year: Int, isFavorite: Boolean): CarItem? {
        TODO()
    }

    override suspend fun fetchByManufacturer(manufacturer: String, isFavorite: Boolean): CarItem? {
        TODO()
    }

    override suspend fun fetchByBrand(brand: String, isFavorite: Boolean): CarItem? {
        TODO()
    }

    override suspend fun fetchByCategory(category: String, isFavorite: Boolean): CarItem? {
        TODO()
    }

    override suspend fun count(isFavorite: Boolean): Int {
        TODO()
    }

    override suspend fun countByModel(model: String, isFavorite: Boolean): Int {
        TODO()
    }

    override suspend fun countByYear(year: Int, isFavorite: Boolean): Int {
        TODO()
    }

    override suspend fun countByManufacturer(manufacturer: String, isFavorite: Boolean): Int {
        TODO()
    }

    override suspend fun countByBrand(brand: String, isFavorite: Boolean): Int {
        TODO()
    }

    override suspend fun countByCategory(category: String, isFavorite: Boolean): Int {
        TODO()
    }

    override suspend fun insert(car: CarItem): CarItem {
        val carObject = supabase.from(CarObj.TABLE).insert(car.toObject().copy(id = null)).decodeSingle<CarObj>()
        val images =
            if (car.images.isNotEmpty()) {
                val realImages = car.images.filterIsInstance<ByteArray>().toSet()
                if (realImages.isNotEmpty()) {
                    uploadImages(car.id, realImages)
                } else {
                    emptySet()
                }
            } else {
                emptySet()
            }
        return carObject.toDomain(images)
    }

    override suspend fun update(car: CarItem): CarItem {
        TODO()
    }

    override suspend fun delete(car: CarItem): Boolean {
        TODO()
    }

    private suspend fun fetchAllImages(
        carId: Uuid,
        userId: String = supabase.auth.currentUserOrNull()!!.id,
        contentType: String = "png",
    ) = supabase.storage.from(CarObj.BUCKET_IMAGES)
        .list("$userId/$carId")
        .map { file ->
            ImageRequest.Builder(context)
                .data(authenticatedStorageItem(CarObj.BUCKET_IMAGES, "$userId/$carId/${file.name}.$contentType"))
                .build()
        }.toSet()

    private suspend fun uploadImages(
        carId: UUID,
        images: Set<ByteArray>,
        userId: String = supabase.auth.currentUserOrNull()!!.id,
    ): Set<String> = coroutineScope {
        val results: MutableSet<String> = ConcurrentSkipListSet()

        images.map { bytes ->
            async {
                val imageId = UUID.randomUUID()
                val path = "$userId/$carId/$imageId.png"

                try {
                    val bucketPath = supabase.storage.from(CarObj.BUCKET_IMAGES).upload(path, bytes) {
                        upsert = true
                    }.path
                    results.add(bucketPath)
                } catch (e: Exception) {
                    Log.e("Car Images", "Failed to upload image", e)
                }
            }
        }.awaitAll()

        results
    }
}
