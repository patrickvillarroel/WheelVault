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
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.uuid.Uuid

class CarSupabaseDataSource(private val supabase: SupabaseClient, private val context: Context) : CarsRepository {
    override suspend fun exist(id: Uuid): Boolean {
        val count = supabase.from(CarObj.TABLE).select {
            filter {
                eq("user_id", supabase.auth.currentUserOrNull()!!.id)
                eq("id", id)
            }
            count(Count.EXACT)
        }.countOrNull() ?: return false

        if (count > 1L) {
            // impossible
            Log.e("Car Supabase", "Exist count find more than 1 car with id $id")
        }

        return count >= 1L
    }

    override suspend fun search(query: String, isFavorite: Boolean): List<CarItem> {
        val cars = supabase
            .from(CarObj.TABLE)
            .select {
                filter {
                    eq(CarObj.USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                    textSearch(
                        CarObj.FULL_TEXT_SEARCH_FIELD,
                        query,
                        textSearchType = TextSearchType.PLAINTO,
                    )
                    if (isFavorite) eq("isFavorite", true)
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<CarObj>()

        val imagesByCarId = fetchAllImages(cars.mapNotNull { it.id })
            .groupBy { it.first }
            .mapValues { (_, list) -> list.map { it.second }.toSet().ifEmpty { setOf(CarItem.EmptyImage) } }

        return cars.map { car ->
            val images = imagesByCarId[car.id] ?: setOf(CarItem.EmptyImage)
            car.toDomain(images)
        }
    }

    override suspend fun fetchAll(isFavorite: Boolean, limit: Int, orderAsc: Boolean): List<CarItem> {
        val cars = supabase.from(CarObj.TABLE).select {
            filter {
                eq(CarObj.USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                if (isFavorite) eq("isFavorite", true)
            }
            limit(limit.toLong())
            if (orderAsc) {
                order("created_at", Order.ASCENDING)
            } else {
                order("created_at", Order.DESCENDING)
            }
        }.decodeList<CarObj>()

        val imagesCars = fetchAllImages(cars.mapNotNull { it.id })
            .groupBy { it.first }
            .mapValues { (_, list) -> list.map { it.second }.toSet().ifEmpty { setOf(CarItem.EmptyImage) } }

        return cars.map { car -> car.toDomain(imagesCars[car.id] ?: setOf(CarItem.EmptyImage)) }
    }

    override suspend fun fetch(id: Uuid): CarItem? = supabase.from(CarObj.TABLE)
        .select {
            filter { eq("id", id) }
            order("created_at", Order.DESCENDING)
        }.decodeSingleOrNull<CarObj>()?.toDomain(
            fetchAllImages(id).ifEmpty { setOf(CarItem.EmptyImage) },
        )

    override suspend fun fetchByModel(model: String, isFavorite: Boolean) = fetchByField("model", model, isFavorite)

    override suspend fun fetchByYear(year: Int, isFavorite: Boolean) = fetchByField("year", year, isFavorite)

    override suspend fun fetchByManufacturer(manufacturer: String, isFavorite: Boolean) =
        fetchByField("manufacturer", manufacturer, isFavorite)

    override suspend fun fetchByBrand(brand: String, isFavorite: Boolean) = fetchByField("brand", brand, isFavorite)

    override suspend fun fetchByCategory(category: String, isFavorite: Boolean) =
        fetchByField("category", category, isFavorite)

    private suspend fun <T : Any> fetchByField(field: String, value: T, isFavorite: Boolean): List<CarItem> {
        val cars =
            supabase
                .from(CarObj.TABLE)
                .select {
                    filter {
                        eq(CarObj.USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                        if (value is String) {
                            ilike(field, "%$value%")
                        } else {
                            eq(field, value)
                        }
                        if (isFavorite) eq("isFavorite", true)
                    }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<CarObj>()

        val carsImages = fetchAllImages(cars.mapNotNull { it.id })
            .groupBy { it.first }
            .mapValues { (_, list) -> list.map { it.second }.toSet().ifEmpty { setOf(CarItem.EmptyImage) } }

        return cars.map { car -> car.toDomain(carsImages[car.id] ?: setOf(CarItem.EmptyImage)) }
    }

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
                if (isFavorite) eq("isFavorite", true)
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
                    eq("id", car.id)
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
                uploadImages(car.id, realImages).ifEmpty { setOf(CarItem.EmptyImage) }
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
                eq("id", car.id)
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

    override suspend fun setAvailableForTrade(carId: Uuid, isAvailable: Boolean): CarItem? {
        val updatedCarObj = supabase.from(CarObj.TABLE)
            .update(mapOf("available_for_trade" to isAvailable)) {
                filter {
                    eq("id", carId)
                    eq(CarObj.USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                }
                select()
            }
            .decodeSingleOrNull<CarObj>()

        return updatedCarObj?.toDomain(
            fetchAllImages(updatedCarObj.id!!).ifEmpty { setOf(CarItem.EmptyImage) },
        )
    }

    suspend fun getCarsForTrade(): List<CarItem> {
        val cars = supabase.from(CarObj.TABLE)
            .select {
                filter {
                    eq("available_for_trade", true)
                    neq(CarObj.USER_ID_FIELD, Uuid.parse(supabase.auth.currentUserOrNull()?.id ?: return emptyList()))
                }
                order("updated_at", Order.DESCENDING)
            }.decodeList<CarObj>()

        val carsImages = fetchAllImages(cars.mapNotNull { it.id })
            .groupBy { it.first }
            .mapValues { (_, list) -> list.map { it.second }.toSet().ifEmpty { setOf(CarItem.EmptyImage) } }

        return cars.map { car -> car.toDomain(carsImages[car.id] ?: setOf(CarItem.EmptyImage)) }
    }

    // FIXME O(n+1)
    private suspend fun fetchAllImages(carId: Uuid) = supabase.postgrest
        .from(CarImagesObj.TABLE)
        .select(Columns.list("storage_path")) {
            filter { eq("car_id", carId) }
        }.decodeList<Map<String, String>>()
        .mapNotNull { image ->
            val path = image.values
            if (path.isEmpty()) return@mapNotNull null
            ImageRequest.Builder(context)
                .data(authenticatedStorageItem(CarObj.BUCKET_IMAGES, path.first()))
                .build()
        }.toSet()

    private suspend fun fetchAllImages(carsId: List<Uuid>) = supabase.postgrest
        .from(CarImagesObj.TABLE)
        .select(Columns.list("car_id", "storage_path")) {
            filter { isIn("car_id", carsId) }
        }
        .decodeList<Map<String, String>>()
        .mapNotNull { image ->
            val carId = image["car_id"]?.let { Uuid.parse(it) } ?: return@mapNotNull null
            val path = image["storage_path"] ?: return@mapNotNull null

            carId to ImageRequest.Builder(context)
                .data(authenticatedStorageItem(CarObj.BUCKET_IMAGES, path))
                .build()
        }

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
