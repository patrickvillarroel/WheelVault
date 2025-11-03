package io.github.patrickvillarroel.wheel.vault.domain.repository

import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import kotlin.uuid.Uuid

/**
 * Repository for cars.
 *
 * @param isFavorite If true, only favorite cars will be returned.
 */
interface CarsRepository {
    suspend fun exist(id: Uuid): Boolean
    suspend fun search(query: String, isFavorite: Boolean = false): List<CarItem>

    suspend fun fetchAll(isFavorite: Boolean = false, limit: Int = 10, orderAsc: Boolean = true): List<CarItem>
    suspend fun fetchAllImage(limit: Int = 10, orderAsc: Boolean = true): Map<Uuid, Any>
    suspend fun fetch(id: Uuid): CarItem?
    suspend fun fetchByModel(model: String, isFavorite: Boolean = false): List<CarItem>
    suspend fun fetchByYear(year: Int, isFavorite: Boolean = false): List<CarItem>
    suspend fun fetchByManufacturer(manufacturer: String, isFavorite: Boolean = false): List<CarItem>
    suspend fun fetchByBrand(brand: String, isFavorite: Boolean = false): List<CarItem>
    suspend fun fetchByCategory(category: String, isFavorite: Boolean = false): List<CarItem>

    suspend fun count(isFavorite: Boolean = false): Int
    suspend fun countByModel(model: String, isFavorite: Boolean = false): Int
    suspend fun countByYear(year: Int, isFavorite: Boolean = false): Int
    suspend fun countByManufacturer(manufacturer: String, isFavorite: Boolean = false): Int
    suspend fun countByBrand(brand: String, isFavorite: Boolean = false): Int
    suspend fun countByCategory(category: String, isFavorite: Boolean = false): Int

    /** Only receive images as ByteArray. */
    suspend fun insert(car: CarItem): CarItem

    /** Only receive images as ByteArray. */
    suspend fun update(car: CarItem): CarItem
    suspend fun delete(car: CarItem): Boolean

    /** Sets the availability of a car for trading. Returns the updated CarItem or null if not found/error. */
    suspend fun setAvailableForTrade(carId: Uuid, isAvailable: Boolean): CarItem?
}
