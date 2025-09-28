package io.github.patrickvillarroel.wheel.vault.data.datasource.room

import io.github.patrickvillarroel.wheel.vault.data.dao.CarDao
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageRepository
import io.github.patrickvillarroel.wheel.vault.data.entity.toDomain
import io.github.patrickvillarroel.wheel.vault.data.entity.toEntity
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import java.util.UUID

class CarRoomDataSource(private val dao: CarDao, private val imageRepository: ImageRepository, private val userId: String) : CarsRepository {

    override suspend fun exist(id: UUID): Boolean =
        dao.exist(id.toString())

    override suspend fun fetchAll(isFavorite: Boolean, limit: Int, orderAsc: Boolean): List<CarItem> {
        val cars = dao.fetchall()
            .filter { it.isFavorite == isFavorite }
            .let { list ->
                if (orderAsc) list.sortedBy { it.createdAt } else list.sortedByDescending { it.createdAt }
            }
            .let { list ->
                if (limit > 0) list.take(limit) else list
            }
        return cars.map {
            it.toDomain(setOf(imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
        }
    }

    override suspend fun fetch(id: UUID): CarItem? =
        dao.fetch(id.toString())?.let { entity ->
            entity.toDomain(setOf(imageRepository.loadImage(entity.idRemote) ?: CarItem.EmptyImage))
        }

    override suspend fun search(query: String, isFavorite: Boolean): List<CarItem> =
        dao.search(query).filter { it.isFavorite == isFavorite }.map {
            it.toDomain(setOf(imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
        }

    override suspend fun fetchByModel(model: String, isFavorite: Boolean): List<CarItem> =
        dao.fetchByModel(model).filter { it.isFavorite == isFavorite }.map {
            it.toDomain(setOf(imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
        }

    override suspend fun fetchByYear(year: Int, isFavorite: Boolean): List<CarItem> =
        dao.fetchByYear(year).filter { it.isFavorite == isFavorite }.map {
            it.toDomain(setOf( imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
        }

    override suspend fun fetchByManufacturer(manufacturer: String, isFavorite: Boolean): List<CarItem> =
        dao.fetchByManufacturer(manufacturer).filter { it.isFavorite == isFavorite }.map {
            it.toDomain(setOf(imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
        }

    override suspend fun fetchByBrand(brand: String, isFavorite: Boolean): List<CarItem> =
        dao.fetchByBrand(brand).filter { it.isFavorite == isFavorite }.map {
            it.toDomain(setOf(imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
        }

    override suspend fun fetchByCategory(category: String, isFavorite: Boolean): List<CarItem> =
        dao.fetchByCategory(category).filter { it.isFavorite == isFavorite }.map {
            it.toDomain(setOf(imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
        }

    override suspend fun count(isFavorite: Boolean): Int =
        dao.fetchall().count { it.isFavorite == isFavorite }

    override suspend fun countByModel(model: String, isFavorite: Boolean): Int =
        dao.fetchByModel(model).count { it.isFavorite == isFavorite }

    override suspend fun countByYear(year: Int, isFavorite: Boolean): Int =
        dao.fetchByYear(year).count { it.isFavorite == isFavorite }

    override suspend fun countByManufacturer(manufacturer: String, isFavorite: Boolean): Int =
        dao.fetchByManufacturer(manufacturer).count { it.isFavorite == isFavorite }

    override suspend fun countByBrand(brand: String, isFavorite: Boolean): Int =
        dao.fetchByBrand(brand).count { it.isFavorite == isFavorite }

    override suspend fun countByCategory(category: String, isFavorite: Boolean): Int =
        dao.fetchByCategory(category).count { it.isFavorite == isFavorite }

    override suspend fun insert(car: CarItem): CarItem {
        dao.insertCar(car.toEntity(userId))
        return car
    }

    override suspend fun update(car: CarItem): CarItem {
        dao.updateCar(car.toEntity(userId))
        return car
    }

    override suspend fun delete(car: CarItem): Boolean =
        dao.deleteCar(car.toEntity(userId)) > 0

    suspend fun saveAll(cars: List<CarItem>, images: List<ByteArray>) {
        cars.forEach { car ->
            dao.insertCar(car.toEntity(userId))
        }
        cars.zip(images).forEach { (car, image) ->
            imageRepository.saveImage(car.id.toString(), image)
        }
    }
    suspend fun countAll(): Int = dao.count()

    suspend fun countByModelDirect(model: String): Int = dao.countByModel(model)

    suspend fun countByYearDirect(year: Int): Int = dao.countByYear(year)

    suspend fun countByManufacturerDirect(manufacturer: String): Int = dao.countByManufacturer(manufacturer)

    suspend fun countByBrandDirect(brand: String): Int = dao.countByBrand(brand)

    suspend fun countByCategoryDirect(category: String): Int = dao.countByCategory(category)
}
