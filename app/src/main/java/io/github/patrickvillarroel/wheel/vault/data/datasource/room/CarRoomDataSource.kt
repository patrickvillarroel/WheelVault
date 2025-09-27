package io.github.patrickvillarroel.wheel.vault.data.datasource.room

import io.github.patrickvillarroel.wheel.vault.data.dao.CarDao
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import java.util.UUID

class CarRoomDataSource(private val dao: CarDao) : CarsRepository {
    override suspend fun exist(id: UUID): Boolean {
        TODO()
    }

    override suspend fun search(query: String, isFavorite: Boolean): List<CarItem> {
        TODO()
    }

    override suspend fun fetchAll(isFavorite: Boolean, limit: Int, orderAsc: Boolean): List<CarItem> {
        TODO()
    }

    override suspend fun fetch(id: UUID): CarItem? {
        TODO()
    }

    override suspend fun fetchByModel(model: String, isFavorite: Boolean): List<CarItem> {
        TODO()
    }

    override suspend fun fetchByYear(year: Int, isFavorite: Boolean): List<CarItem> {
        TODO()
    }

    override suspend fun fetchByManufacturer(manufacturer: String, isFavorite: Boolean): List<CarItem> {
        TODO()
    }

    override suspend fun fetchByBrand(brand: String, isFavorite: Boolean): List<CarItem> {
        TODO()
    }

    override suspend fun fetchByCategory(category: String, isFavorite: Boolean): List<CarItem> {
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
        TODO()
    }

    override suspend fun update(car: CarItem): CarItem {
        TODO()
    }

    override suspend fun delete(car: CarItem): Boolean {
        TODO()
    }
}
