package io.github.patrickvillarroel.wheel.vault.data

import io.github.patrickvillarroel.wheel.vault.data.datasource.CarSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import java.util.UUID

data class CarRepositoryImpl(private val supabase: CarSupabaseDataSource) : CarsRepository {
    override suspend fun search(query: String, isFavorite: Boolean): List<CarItem> = supabase.search(query, isFavorite)

    override suspend fun fetchAll(isFavorite: Boolean, limit: Int): List<CarItem> = supabase.fetchAll(isFavorite, limit)

    override suspend fun fetch(id: UUID): CarItem? = supabase.fetch(id)

    override suspend fun fetchByModel(model: String, isFavorite: Boolean) = supabase.fetchByModel(model, isFavorite)

    override suspend fun fetchByYear(year: Int, isFavorite: Boolean) = supabase.fetchByYear(year, isFavorite)

    override suspend fun fetchByManufacturer(manufacturer: String, isFavorite: Boolean) =
        supabase.fetchByManufacturer(manufacturer, isFavorite)

    override suspend fun fetchByBrand(brand: String, isFavorite: Boolean) = supabase.fetchByBrand(brand, isFavorite)

    override suspend fun fetchByCategory(category: String, isFavorite: Boolean) =
        supabase.fetchByCategory(category, isFavorite)

    override suspend fun count(isFavorite: Boolean): Int = supabase.count(isFavorite)

    override suspend fun countByModel(model: String, isFavorite: Boolean): Int =
        supabase.countByModel(model, isFavorite)

    override suspend fun countByYear(year: Int, isFavorite: Boolean): Int = supabase.countByYear(year, isFavorite)

    override suspend fun countByManufacturer(manufacturer: String, isFavorite: Boolean): Int =
        supabase.countByManufacturer(manufacturer, isFavorite)

    override suspend fun countByBrand(brand: String, isFavorite: Boolean): Int =
        supabase.countByBrand(brand, isFavorite)

    override suspend fun countByCategory(category: String, isFavorite: Boolean): Int =
        supabase.countByCategory(category, isFavorite)

    override suspend fun insert(car: CarItem): CarItem = supabase.insert(car)

    override suspend fun update(car: CarItem): CarItem = supabase.update(car)

    override suspend fun delete(car: CarItem): Boolean = supabase.delete(car)
}
