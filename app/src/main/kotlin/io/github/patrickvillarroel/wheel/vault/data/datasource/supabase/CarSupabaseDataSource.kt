package io.github.patrickvillarroel.wheel.vault.data.datasource.supabase

import co.touchlab.kermit.Logger
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
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.ktor.http.ContentType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.uuid.Uuid
import coil3.PlatformContext as CoilContext

class CarSupabaseDataSource(private val supabase: SupabaseClient, private val context: CoilContext) :
    CarsRepository {
    override suspend fun exist(id: Uuid): Boolean {
        val count = supabase.from(TABLE).select {
            filter {
                eq("user_id", supabase.auth.currentUserOrNull()!!.id)
                eq("id", id)
            }
            count(Count.EXACT)
        }.countOrNull() ?: return false

        if (count > 1L) {
            // impossible
            logger.e { "Exist count find more than 1 car with id $id" }
        }

        return count >= 1L
    }

    override suspend fun search(query: String, isFavorite: Boolean): List<CarItem> {
        val cars = supabase
            .from(TABLE)
            .select {
                filter {
                    eq(USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                    textSearch(
                        FULL_TEXT_SEARCH_FIELD,
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
        val cars = supabase.from(TABLE).select {
            filter {
                eq(USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
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

    override suspend fun fetchAllImage(limit: Int, orderAsc: Boolean): Map<Uuid, Any> {
        val carsId = supabase.from(TABLE).select(Columns.list("id")) {
            filter {
                eq(USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
            }
            limit(limit.toLong())
            if (orderAsc) {
                order("created_at", Order.ASCENDING)
            } else {
                order("created_at", Order.DESCENDING)
            }
        }.decodeList<Map<String, Uuid>>().flatMap { it.values }
        val imagesCars = fetchAllImages(carsId)
            .groupBy { it.first }
            .mapValues { (_, list) -> list.map { it.second }.toSet().ifEmpty { setOf(CarItem.EmptyImage) }.first() }
        return imagesCars
    }

    override suspend fun fetch(id: Uuid): CarItem? = supabase.from(TABLE)
    override fun fetchAllPaged(isFavorite: Boolean, orderAsc: Boolean): PagedSource<Int, CarItem> =
        PagedSource { key, size ->
            val offset = key ?: 0

            val cars = supabase.from(CarObj.TABLE).select {
                filter {
                    eq(CarObj.USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                    if (isFavorite) {
                        CarObj::isFavorite eq true
                    }
                }
                limit(size.toLong())
                range(offset.toLong(), (offset + size - 1).toLong())
                if (orderAsc) {
                    order("created_at", Order.ASCENDING)
                } else {
                    order("created_at", Order.DESCENDING)
                }
            }.decodeList<CarObj>()

            val imagesCars = fetchAllImages(cars.mapNotNull { it.id })
                .groupBy { it.first }
                .mapValues { (_, list) -> list.map { it.second }.toSet().ifEmpty { setOf(CarItem.EmptyImage) } }

            val data = cars.map { car -> car.toDomain(imagesCars[car.id] ?: setOf(CarItem.EmptyImage)) }

            val nextKey = if (data.size < size) null else offset + size
            val prevKey = if (offset == 0) null else maxOf(offset - size, 0)

            Page(data = data, prevKey = prevKey, nextKey = nextKey)
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
                .from(TABLE)
                .select {
                    filter {
                        eq(USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
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
        .from(TABLE)
        .select {
            count(count = Count.EXACT)
            filter {
                eq(USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                if (field != null && value != null) eq(field, value)
                if (isFavorite) eq("isFavorite", true)
            }
        }
        .countOrNull()?.toInt()
        ?: 0

    override suspend fun insert(car: CarItem): CarItem {
        logger.i { "Inserting car $car" }
        val carObject = supabase.from(TABLE)
            .insert(car.toObject()) {
                select()
            }.decodeSingleOrNull<CarObj>() ?: error("Car not found after insert it")

        val images = if (car.images.isNotEmpty()) {
            val realImages = car.images.filterIsInstance<ByteArray>().toSet()

            if (realImages.isNotEmpty()) {
                logger.i { "Uploading images for car ${car.id}" }
                uploadImages(carObject.id!!, realImages).ifEmpty { setOf(CarItem.EmptyImage) }
            } else {
                logger.i { "No images to upload for car ${car.id} after filter is ByteArray" }
                setOf(CarItem.EmptyImage)
            }
        } else {
            logger.i { "No images to upload for car ${car.id}" }
            setOf(CarItem.EmptyImage)
        }

        return carObject.toDomain(images)
    }

    override suspend fun update(car: CarItem): CarItem {
        val updated = supabase.from(TABLE)
            .update(car.toObject()) {
                filter {
                    eq("id", car.id)
                    eq(USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                }
                select()
            }
            .decodeSingleOrNull<CarObj>()
            ?: run {
                logger.e { "update return null, manual fetching" }
                return fetch(car.id) ?: error("Car not found after update it")
            }

        val images = if (car.images.isNotEmpty()) {
            val realImages = car.images.filterIsInstance<ByteArray>().toSet()

            if (realImages.isNotEmpty()) {
                logger.i { "Uploading images for car ${car.id}" }
                uploadImages(car.id, realImages).ifEmpty { setOf(CarItem.EmptyImage) }
            } else {
                logger.i { "No images to upload for car ${car.id} after filter is ByteArray" }
                setOf(CarItem.EmptyImage)
            }
        } else {
            logger.i { "No images to upload for car ${car.id}" }
            setOf(CarItem.EmptyImage)
        }

        return updated.toDomain(images)
    }

    override suspend fun delete(car: CarItem): Boolean {
        supabase.from(TABLE).delete {
            filter {
                eq("id", car.id)
                eq(USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
            }
        }

        val currentUserId = supabase.auth.currentUserOrNull()!!.id
        val imagePaths = supabase.storage.from(BUCKET_IMAGES)
            .list("$currentUserId/${car.id}")
            .map { "$currentUserId/${car.id}/${it.name}" }

        if (imagePaths.isNotEmpty()) {
            supabase.storage.from(BUCKET_IMAGES).delete(imagePaths)
        }

        return true
    }

    override suspend fun setAvailableForTrade(carId: Uuid, isAvailable: Boolean): CarItem? {
        val updatedCarObj = supabase.from(TABLE)
            .update(mapOf("available_for_trade" to isAvailable)) {
                filter {
                    eq("id", carId)
                    eq(USER_ID_FIELD, supabase.auth.currentUserOrNull()!!.id)
                }
                select()
            }
            .decodeSingleOrNull<CarObj>()

        return updatedCarObj?.toDomain(
            fetchAllImages(updatedCarObj.id!!).ifEmpty { setOf(CarItem.EmptyImage) },
        )
    }

    suspend fun getCarsForTrade(): List<CarItem> {
        val cars = supabase.from(TABLE)
            .select {
                filter {
                    eq("available_for_trade", true)
                    neq(USER_ID_FIELD, Uuid.parse(supabase.auth.currentUserOrNull()?.id ?: return emptyList()))
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
                .data(authenticatedStorageItem(BUCKET_IMAGES, path.first()))
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
                .data(authenticatedStorageItem(BUCKET_IMAGES, path))
                .build()
        }

    private suspend fun uploadImages(
        carId: Uuid,
        images: Set<ByteArray>,
        userId: String = supabase.auth.currentUserOrNull()!!.id,
    ) = coroutineScope {
        val successfulUploads = images.map { bytes ->
            async {
                val imageId = Uuid.random()
                val path = "$userId/$carId/$imageId.webp"

                try {
                    supabase.storage.from(BUCKET_IMAGES).upload(path, bytes) {
                        upsert = true
                        contentType = ContentType.Image.WEBP
                    }
                    CarImagesObj(
                        id = imageId,
                        carId = carId,
                        storagePath = path,
                        mimeType = "image/webp",
                        uploadedBy = Uuid.parse(userId),
                    )
                } catch (e: Exception) {
                    currentCoroutineContext().ensureActive()
                    logger.e(e) { "Upload failed for $path" }
                    null
                }
            }
        }.awaitAll().filterNotNull()

        if (successfulUploads.isEmpty()) {
            logger.w { "No images uploaded successfully for car $carId" }
            return@coroutineScope emptySet()
        }

        val response = supabase.postgrest
            .from(CarImagesObj.TABLE)
            .upsert(successfulUploads) {
                select(Columns.list("storage_path"))
            }
            .decodeList<Map<String, String>>()
            .flatMap { it.values }

        logger.i { "Inserted ${response.size} image records for car $carId" }

        response.map { storagePath ->
            ImageRequest.Builder(context)
                .data(authenticatedStorageItem(BUCKET_IMAGES, storagePath))
                .build()
        }.toSet()
    }

    companion object {
        private const val TABLE = "cars"
        private const val BUCKET_IMAGES = "cars-images"
        private const val USER_ID_FIELD = "user_id"
        private const val FULL_TEXT_SEARCH_FIELD = "document_with_weights"
        private val logger = Logger.withTag("Cars Supabase DataSource")

        private fun CarItem.toObject() = CarObj(
            id = this.id,
            model = this.model,
            year = this.year,
            brand = this.brand,
            manufacturer = this.manufacturer,
            category = this.category,
            description = this.description,
            quantity = this.quantity,
            isFavorite = this.isFavorite,
            availableForTrade = this.availableForTrade,
        )

        private fun CarObj.toDomain(images: Set<Any>) = CarItem(
            id = this.id!!,
            model = this.model,
            year = this.year,
            brand = this.brand,
            manufacturer = this.manufacturer,
            category = this.category,
            description = this.description,
            quantity = this.quantity,
            isFavorite = this.isFavorite,
            availableForTrade = this.availableForTrade,
            images = images,
        )
    }
}
