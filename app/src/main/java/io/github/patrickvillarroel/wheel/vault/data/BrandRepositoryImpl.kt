package io.github.patrickvillarroel.wheel.vault.data

import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.BrandSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import java.util.UUID

/**
 * Brands repository.
 *
 * This handle between data-sources, current only delegate to supabase.
 */
class BrandRepositoryImpl(private val supabase: BrandSupabaseDataSource) : BrandRepository {
    override suspend fun search(query: String): List<Brand> = supabase.search(query)

    override suspend fun fetchAll(): List<Brand> = supabase.fetchAll()

    override suspend fun fetch(id: UUID): Brand? = supabase.fetch(id)

    override suspend fun fetchByName(name: String): Brand? = supabase.fetchByName(name)

    override suspend fun fetchByDescription(description: String): Brand? = supabase.fetchByDescription(description)
}
