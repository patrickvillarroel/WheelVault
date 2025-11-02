package io.github.patrickvillarroel.wheel.vault.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.patrickvillarroel.wheel.vault.data.entity.BrandEntity

@Dao
interface BrandDao {
    @Query("SELECT * FROM brands WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<BrandEntity>

    @Query("SELECT * FROM brands WHERE id = :id LIMIT 1")
    suspend fun fetchById(id: String): BrandEntity?

    @Query("SELECT * FROM brands ORDER BY created_at DESC")
    suspend fun fetchAll(): List<BrandEntity>

    @Query("SELECT name FROM brands ORDER BY created_at DESC")
    suspend fun fetchAllNames(): List<String>

    @Query("SELECT * FROM brands WHERE name = :name LIMIT 1")
    suspend fun fetchByName(name: String): BrandEntity?

    @Query("SELECT * FROM brands WHERE description = :description LIMIT 1")
    suspend fun fetchByDescription(description: String): BrandEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brand: BrandEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBrands(brands: List<BrandEntity>)
}
