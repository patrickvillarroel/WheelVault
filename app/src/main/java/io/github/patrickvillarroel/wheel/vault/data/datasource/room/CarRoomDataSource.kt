package io.github.patrickvillarroel.wheel.vault.data.datasource.room

import io.github.patrickvillarroel.wheel.vault.data.dao.CarDao
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageRepository
import io.github.patrickvillarroel.wheel.vault.data.entity.toDomain
import io.github.patrickvillarroel.wheel.vault.data.entity.toEntity
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import java.util.UUID

class CarRoomDataSource(private val dao: CarDao, private val imageRepository: ImageRepository) : CarsRepository {
    override suspend fun exist(id: UUID): Boolean = dao.exist(id.toString())

    override suspend fun fetchAll(isFavorite: Boolean, limit: Int, orderAsc: Boolean): List<CarItem> {
        val cars = if (isFavorite) {
            if (orderAsc) {
                dao.fetchFavoritesOrderByCreatedAsc()
            } else {
                dao.fetchFavoritesOrderByCreatedDesc()
            }
        } else {
            if (orderAsc) {
                dao.fetchAllOrderByCreatedAsc()
            } else {
                dao.fetchAllOrderByCreatedDesc()
            }
        }
        return cars.let { list ->
            // TODO filter in ROOM
            if (limit > 0) list.take(limit) else list
        }.map {
            it.toDomain(setOf(imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
        }
    }

    // TODO fix this method
    override suspend fun fetch(id: UUID): CarItem? = dao.fetchById(id.toString())?.let { entity ->
        entity.toDomain(setOf(imageRepository.loadImage(entity.idRemote) ?: CarItem.EmptyImage))
    }

    override suspend fun search(query: String, isFavorite: Boolean): List<CarItem> = dao.search(query).let {
        // TODO add overload method on DAO to filter by favorite
        if (isFavorite) it.filter { car -> car.isFavorite } else it
    }.map {
        it.toDomain(setOf(imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
    }

    override suspend fun fetchByModel(model: String, isFavorite: Boolean): List<CarItem> =
        (if (isFavorite) dao.fetchFavoritesByModel(model) else dao.fetchByModel(model))
            .map {
                it.toDomain(setOf(imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
            }

    override suspend fun fetchByYear(year: Int, isFavorite: Boolean): List<CarItem> =
        (if (isFavorite) dao.fetchFavoritesByYear(year) else dao.fetchByYear(year))
            .map {
                it.toDomain(setOf(imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
            }

    override suspend fun fetchByManufacturer(manufacturer: String, isFavorite: Boolean): List<CarItem> = (
        if (isFavorite) {
            dao.fetchFavoritesByManufacturer(manufacturer)
        } else {
            dao.fetchByManufacturer(manufacturer)
        }
        ).map {
        it.toDomain(setOf(imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
    }

    override suspend fun fetchByBrand(brand: String, isFavorite: Boolean): List<CarItem> = (
        if (isFavorite) dao.fetchFavoritesByBrand(brand) else dao.fetchByBrand(brand)
        ).map {
        it.toDomain(setOf(imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
    }

    override suspend fun fetchByCategory(category: String, isFavorite: Boolean): List<CarItem> = (
        if (isFavorite) dao.fetchFavoritesByCategory(category) else dao.fetchByCategory(category)
        ).map {
        it.toDomain(setOf(imageRepository.loadImage(it.idRemote) ?: CarItem.EmptyImage))
    }

    override suspend fun count(isFavorite: Boolean): Int = if (isFavorite) dao.countFavorites() else dao.count()

    override suspend fun countByModel(model: String, isFavorite: Boolean): Int =
        if (isFavorite) dao.countFavoritesByModel(model) else dao.countByModel(model)

    override suspend fun countByYear(year: Int, isFavorite: Boolean): Int =
        if (isFavorite) dao.countFavoritesByYear(year) else dao.countByYear(year)

    override suspend fun countByManufacturer(manufacturer: String, isFavorite: Boolean): Int =
        if (isFavorite) dao.countFavoritesByManufacturer(manufacturer) else dao.countByManufacturer(manufacturer)

    override suspend fun countByBrand(brand: String, isFavorite: Boolean): Int =
        if (isFavorite) dao.countFavoritesByBrand(brand) else dao.countByBrand(brand)

    override suspend fun countByCategory(category: String, isFavorite: Boolean): Int =
        if (isFavorite) dao.countFavoritesByCategory(category) else dao.countByCategory(category)

    override suspend fun insert(car: CarItem): CarItem {
        dao.insertCar(car.toEntity(null))
        // TODO add image
        return car
    }

    override suspend fun update(car: CarItem): CarItem {
        dao.updateCar(car.toEntity(null))
        // TODO add image
        return car
    }

    override suspend fun delete(car: CarItem): Boolean = dao.deleteCar(car.toEntity(null)) > 0
}
