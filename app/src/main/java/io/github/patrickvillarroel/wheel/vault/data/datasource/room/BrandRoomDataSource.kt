package io.github.patrickvillarroel.wheel.vault.data.datasource.room

import io.github.patrickvillarroel.wheel.vault.data.dao.BrandDao
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import java.util.UUID

class BrandRoomDataSource(private val dao: BrandDao) : BrandRepository {
    override suspend fun search(query: String): List<Brand> {
        TODO()
    }

    override suspend fun fetchAll(): List<Brand> {
        TODO()
    }

    override suspend fun fetch(id: UUID): Brand? {
        TODO()
    }

    override suspend fun fetchByName(name: String): Brand? {
        TODO()
    }

    override suspend fun fetchByDescription(description: String): Brand? {
        TODO()
    }
    suspend fun insert(brand: Brand): Brand {
        TODO()
    }
}
