package io.github.patrickvillarroel.wheel.vault.data.datasource.room

import io.github.patrickvillarroel.wheel.vault.data.dao.BrandDao
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageRepository
import io.github.patrickvillarroel.wheel.vault.data.entity.toDomain
import io.github.patrickvillarroel.wheel.vault.data.entity.toEntity
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import kotlin.uuid.Uuid

class BrandRoomDataSource(private val dao: BrandDao, private val imageRepository: ImageRepository) : BrandRepository {
    override suspend fun fetchAll(forceRefresh: Boolean): List<Brand> = dao.fetchAll().map {
        it.toDomain(imageRepository.loadImage(it.id) ?: Brand.DEFAULT_IMAGE)
    }

    override suspend fun fetch(id: Uuid): Brand? = dao.fetchById(id.toString())?.let { entity ->
        entity.toDomain(imageRepository.loadImage(entity.id) ?: Brand.DEFAULT_IMAGE)
    }

    override suspend fun search(query: String): List<Brand> = dao.search(query).map {
        it.toDomain(imageRepository.loadImage(it.id) ?: Brand.DEFAULT_IMAGE)
    }

    override suspend fun fetchAllNames(forceRefresh: Boolean): List<String> = dao.fetchAllNames()

    override suspend fun fetchByName(name: String): Brand? = dao.fetchByName(name)?.let {
        it.toDomain(imageRepository.loadImage(it.id) ?: Brand.DEFAULT_IMAGE)
    }

    override suspend fun fetchByDescription(description: String): Brand? = dao.fetchByDescription(description)?.let {
        it.toDomain(imageRepository.loadImage(it.id) ?: Brand.DEFAULT_IMAGE)
    }

    suspend fun saveAll(brands: List<Brand>, images: List<ByteArray>) {
        dao.insertAllBrands(brands.map { it.toEntity() })
        brands.zip(images).forEach { (brand, image) ->
            imageRepository.saveImage(brand.id.toString(), image)
        }
    }
}
