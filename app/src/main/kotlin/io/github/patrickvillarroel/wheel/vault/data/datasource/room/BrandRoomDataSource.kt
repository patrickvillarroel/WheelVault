package io.github.patrickvillarroel.wheel.vault.data.datasource.room

import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.data.dao.BrandDao
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageRepository
import io.github.patrickvillarroel.wheel.vault.data.entity.toDomain
import io.github.patrickvillarroel.wheel.vault.data.entity.toEntity
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import kotlin.uuid.Uuid

class BrandRoomDataSource(private val dao: BrandDao, private val imageRepository: ImageRepository) : BrandRepository {
    override suspend fun fetchAll(forceRefresh: Boolean): List<Brand> = dao.fetchAll().map { brandEntity ->
        brandEntity.toDomain(imageRepository.loadImage(brandEntity.id + ".png") ?: Brand.DEFAULT_IMAGE)
    }

    override suspend fun fetch(id: Uuid, forceRefresh: Boolean): Brand? = dao.fetchById(id.toString())?.let { entity ->
        entity.toDomain(imageRepository.loadImage(entity.id + ".png") ?: Brand.DEFAULT_IMAGE)
    }

    override suspend fun search(query: String): List<Brand> = dao.search(query).map { brandEntity ->
        brandEntity.toDomain(imageRepository.loadImage(brandEntity.id + ".png") ?: Brand.DEFAULT_IMAGE)
    }

    override suspend fun fetchAllNames(forceRefresh: Boolean): List<String> = dao.fetchAllNames()
    override suspend fun fetchAllImages(forceRefresh: Boolean): Map<Uuid, Any> = dao.fetchAllIds().associate { id ->
        Uuid.parse(id) to (imageRepository.loadImage("$id.png")?.first() ?: Brand.DEFAULT_IMAGE)
    }

    override suspend fun fetchByName(name: String): Brand? = dao.fetchByName(name)?.let { brandEntity ->
        brandEntity.toDomain(imageRepository.loadImage(brandEntity.id + ".png") ?: Brand.DEFAULT_IMAGE)
    }

    override suspend fun fetchByDescription(description: String): Brand? =
        dao.fetchByDescription(description)?.let { brandEntity ->
            brandEntity.toDomain(imageRepository.loadImage(brandEntity.id + ".png") ?: Brand.DEFAULT_IMAGE)
        }

    suspend fun save(brand: Brand, image: ByteArray?) {
        logger.d { "Saving brand to room with id='${brand.id}'" }
        dao.insertBrand(brand.toEntity())
        if (image != null) {
            logger.d { "Saving image of brand with id='${brand.id}' as png image" }
            imageRepository.saveImage(brand.id.toString() + ".png", image)
        }
    }

    suspend fun saveAll(brands: List<Brand>, images: List<ByteArray>) {
        logger.d { "Saving ${brands.size} brands to room" }
        dao.insertAllBrands(brands.map { it.toEntity() })
        brands.zip(images).forEach { (brand, image) ->
            imageRepository.saveImage(brand.id.toString(), image)
        }
    }

    companion object {
        private val logger = Logger.withTag("BrandRoomDataSource")
    }
}
