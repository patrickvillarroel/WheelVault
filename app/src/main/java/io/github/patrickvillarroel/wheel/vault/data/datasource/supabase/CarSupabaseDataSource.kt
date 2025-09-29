@file:OptIn(ExperimentalUuidApi::class)

package io.github.patrickvillarroel.wheel.vault.data.datasource.supabase

import android.content.Context
import android.util.Log
import coil3.request.ImageRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.TextSearchType
import io.github.jan.supabase.storage.authenticatedStorageItem
import io.github.jan.supabase.storage.storage
import io.github.patrickvillarroel.wheel.vault.data.objects.CarImagesObj
import io.github.patrickvillarroel.wheel.vault.data.objects.CarObj
import io.github.patrickvillarroel.wheel.vault.data.objects.toDomain
import io.github.patrickvillarroel.wheel.vault.data.objects.toObject
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.ktor.http.ContentType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.util.UUID
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

class CarSupabaseDataSource(private val supabase: SupabaseClient, private val context: Context) : CarsRepository {
    override suspend fun exist(id: UUID): Boolean {
        val count = supabase.from(CarObj.TABLE).select {
            filter {
                eq("user_id", supabase.auth.currentUserOrNull()!!.id)
                CarObj::id eq id.toKotlinUuid()
            }
            count(Count.EXACT)
        }.countOrNull() ?: return false

        if (count > 1L) {
            // impossible
            Log.e("Car Supabase", "Exist count find more than 1 car with id $id")
        }

        return count >= 1L
    }

    override suspend fun search(query: String, isFavorite: Boolean): List<CarItem> = supabase
        .from(CarObj.TABLE)
        .select {
            filter {
                eq(CarObj.USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                textSearch(
                    CarObj.FULL_TEXT_SEARCH_FIELD,
                    query,
                    textSearchType = TextSearchType.PLAINTO,
                )
                if (isFavorite) CarObj::isFavorite eq true
            }
            order("created_at", Order.DESCENDING)
        }
        .decodeList<CarObj>()
        .map { it.toDomain(fetchAllImages(it.id!!).ifEmpty { setOf(CarItem.EmptyImage) }) }

    override suspend fun fetchAll(isFavorite: Boolean, limit: Int, orderAsc: Boolean): List<CarItem> =
        supabase.from(CarObj.TABLE).select {
            filter {
                eq(CarObj.USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                if (isFavorite) {
                    CarObj::isFavorite eq true
                }
            }
            limit(limit.toLong())
            if (orderAsc) {
                order("created_at", Order.ASCENDING)
            } else {
                order("created_at", Order.DESCENDING)
            }
        }.decodeList<CarObj>().map { it.toDomain(fetchAllImages(it.id!!).ifEmpty { setOf(CarItem.EmptyImage) }) }

    override suspend fun fetch(id: UUID): CarItem? = supabase.from(CarObj.TABLE)
        .select {
            filter {
                eq(CarObj.USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                CarObj::id eq id.toKotlinUuid()
            }
            order("created_at", Order.DESCENDING)
        }.decodeSingleOrNull<CarObj>()?.toDomain(
            fetchAllImages(id.toKotlinUuid()).ifEmpty {
                setOf(CarItem.EmptyImage)
            },
        )

    override suspend fun fetchByModel(model: String, isFavorite: Boolean) = fetchByField("model", model, isFavorite)

    override suspend fun fetchByYear(year: Int, isFavorite: Boolean) = fetchByField("year", year, isFavorite)

    override suspend fun fetchByManufacturer(manufacturer: String, isFavorite: Boolean) =
        fetchByField("manufacturer", manufacturer, isFavorite)

    override suspend fun fetchByBrand(brand: String, isFavorite: Boolean) = fetchByField("brand", brand, isFavorite)

    override suspend fun fetchByCategory(category: String, isFavorite: Boolean) =
        fetchByField("category", category, isFavorite)

    private suspend fun <T : Any> fetchByField(field: String, value: T, isFavorite: Boolean) = supabase
        .from(CarObj.TABLE)
        .select {
            filter {
                eq(CarObj.USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                if (value is String) {
                    ilike(field, "%$value%")
                } else {
                    eq(field, value)
                }
                if (isFavorite) CarObj::isFavorite eq true
            }
            order("created_at", Order.DESCENDING)
        }
        .decodeList<CarObj>()
        .map { it.toDomain(fetchAllImages(it.id!!).ifEmpty { setOf(CarItem.EmptyImage) }) }

    override suspend fun count(isFavorite: Boolean): Int = countByField(null, null, isFavorite)

    override suspend fun countByModel(model: String, isFavorite: Boolean): Int =
        countByField("model", model, isFavorite)

    override suspend fun countByYear(year: Int, isFavorite: Boolean): Int = countByField("year", year, isFavorite)

    override suspend fun countByManufacturer(manufacturer: String, isFavorite: Boolean): Int =
        countByField("manufacturer", manufacturer, isFavorite)

    override suspend fun countByBrand(brand: String, isFavorite: Boolean): Int =
        countByField("brand", brand, isFavorite)

    override suspend fun countByCategory(category: String, isFavorite: Boolean): Int =
        countByField("category", category, isFavorite)

    private suspend fun <T : Any> countByField(field: String?, value: T?, isFavorite: Boolean): Int = supabase
        .from(CarObj.TABLE)
        .select {
            count(count = Count.EXACT)
            filter {
                eq(CarObj.USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                if (field != null && value != null) eq(field, value)
                if (isFavorite) CarObj::isFavorite eq true
            }
        }
        .countOrNull()?.toInt()
        ?: 0

    override suspend fun insert(car: CarItem): CarItem {
        Log.i("Car Supabase", "Inserting car $car")
        val carObject = supabase.from(CarObj.TABLE)
            .insert(car.toObject().copy(id = null)) {
                select()
            }.decodeSingleOrNull<CarObj>() ?: error("Car not found after insert it")

        val images = if (car.images.isNotEmpty()) {
            val realImages = car.images.filterIsInstance<ByteArray>().toSet()

            if (realImages.isNotEmpty()) {
                Log.i("Car Supabase", "Uploading images for car ${car.id}")
                uploadImages(carObject.id!!, realImages).ifEmpty { setOf(CarItem.EmptyImage) }
            } else {
                Log.i("Car Supabase", "No images to upload for car ${car.id} after filter is ByteArray")
                setOf(CarItem.EmptyImage)
            }
        } else {
            Log.i("Car Supabase", "No images to upload for car ${car.id}")
            setOf(CarItem.EmptyImage)
        }

        return carObject.toDomain(images)
    }

    override suspend fun update(car: CarItem): CarItem {
        val updated = supabase.from(CarObj.TABLE)
            .update(car.toObject()) {
                filter {
                    CarObj::id eq car.id.toKotlinUuid()
                    eq(CarObj.USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                }
                select()
            }
            .decodeSingleOrNull<CarObj>()
            ?: run {
                Log.e("Car Supabase", "update return null, manual fetching")
                return fetch(car.id) ?: error("Car not found after update it")
            }

        val images = if (car.images.isNotEmpty()) {
            val realImages = car.images.filterIsInstance<ByteArray>().toSet()

            if (realImages.isNotEmpty()) {
                Log.i("Car Supabase", "Uploading images for car ${car.id}")
                uploadImages(car.id.toKotlinUuid(), realImages).ifEmpty { setOf(CarItem.EmptyImage) }
            } else {
                Log.i("Car Supabase", "No images to upload for car ${car.id} after filter is ByteArray")
                setOf(CarItem.EmptyImage)
            }
        } else {
            Log.i("Car Supabase", "No images to upload for car ${car.id}")
            setOf(CarItem.EmptyImage)
        }

        return updated.toDomain(images)
    }

    override suspend fun delete(car: CarItem): Boolean {
        supabase.from(CarObj.TABLE).delete {
            filter {
                CarObj::id eq car.id.toKotlinUuid()
                eq(CarObj.USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
            }
        }

        val currentUserId = supabase.auth.currentUserOrNull()!!.id
        val imagePaths = supabase.storage.from(CarObj.BUCKET_IMAGES)
            .list("$currentUserId/${car.id}")
            .map { "$currentUserId/${car.id}/${it.name}" }

        if (imagePaths.isNotEmpty()) {
            supabase.storage.from(CarObj.BUCKET_IMAGES).delete(imagePaths)
        }

        return true
    }

    suspend fun getCarsForTrade() = supabase.from(CarObj.TABLE)
        .select {
            filter {
                eq("available_for_trade", true)
            }
            order("updated_at", Order.DESCENDING)
        }.decodeList<CarObj>()
        .map { it.toDomain(fetchAllImages(it.id!!).ifEmpty { setOf(CarItem.EmptyImage) }) }

    // FIXME O(n+1)
    private suspend fun fetchAllImages(carId: Uuid) = supabase.postgrest
        .from(CarImagesObj.TABLE)
        .select(Columns.list("storage_path")) {
            filter {
                CarImagesObj::carId eq carId
            }
        }.decodeList<Map<String, String>>()
        .mapNotNull { image ->
            val path = image.values
            if (path.isEmpty()) return@mapNotNull null
            ImageRequest.Builder(context)
                .data(authenticatedStorageItem(CarObj.BUCKET_IMAGES, path.first()))
                .build()
        }.toSet()

    private suspend fun uploadImages(
        carId: Uuid,
        images: Set<ByteArray>,
        userId: String = supabase.auth.currentUserOrNull()!!.id,
    ) = coroutineScope {
        val results: MutableSet<Pair<Uuid, String>> = ConcurrentSkipListSet()

        images.map { bytes ->
            async {
                val imageId = Uuid.random()
                val path = "$userId/$carId/$imageId.webp"

                try {
                    supabase.storage.from(CarObj.BUCKET_IMAGES).upload(path, bytes) {
                        upsert = true
                        contentType = ContentType.Image.WEBP
                    }
                    results.add(imageId to path)
                } catch (e: Exception) {
                    currentCoroutineContext().ensureActive()
                    Log.e("Car Images", "Failed to upload image", e)
                }
            }
        }.awaitAll()

        supabase.postgrest
            .from(CarImagesObj.TABLE)
            .upsert(
                results.map { (imageId, fullPath) ->
                    CarImagesObj(
                        carId = carId,
                        storagePath = fullPath,
                        mimeType = "image/webp",
                        uploadedBy = Uuid.parse(userId),
                        id = imageId,
                    )
                },
            )

        results.map { (_, path) ->
            ImageRequest.Builder(context)
                .data(authenticatedStorageItem(CarObj.BUCKET_IMAGES, path))
                .build()
        }.toSet()
    }
}
