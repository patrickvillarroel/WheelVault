package io.github.patrickvillarroel.wheel.vault.data.dao

import androidx.room.*
import io.github.patrickvillarroel.wheel.vault.data.entity.CarEntity

@Dao
interface CarDao {
    @Query("SELECT EXISTS(SELECT 1 FROM cars WHERE id = :id)")
    suspend fun exist(id: String): Boolean

    @Query(
        """
        SELECT * FROM cars
        WHERE model LIKE '%' || :query || '%'
           OR brand LIKE '%' || :query || '%'
           OR manufacturer LIKE '%' || :query || '%'
           OR category LIKE '%' || :query || '%'
           OR description LIKE '%' || :query || '%'
        ORDER BY created_at DESC
    """,
    )
    suspend fun search(query: String): List<CarEntity>

    @Query("SELECT * FROM cars ORDER BY created_at DESC")
    suspend fun fetchall(): List<CarEntity>

    @Query("SELECT * FROM cars WHERE id = :id LIMIT 1")
    suspend fun fetch(id: String): CarEntity?

    @Query("SELECT * FROM cars WHERE model = :model ORDER BY created_at DESC")
    suspend fun fetchByModel(model: String): List<CarEntity>

    @Query("SELECT * FROM cars WHERE year = :year ORDER BY created_at DESC")
    suspend fun fetchByYear(year: Int): List<CarEntity>

    @Query("SELECT * FROM cars WHERE manufacturer = :manufacturer ORDER BY created_at DESC")
    suspend fun fetchByManufacturer(manufacturer: String): List<CarEntity>

    @Query("SELECT * FROM cars WHERE brand = :brand ORDER BY created_at DESC")
    suspend fun fetchByBrand(brand: String): List<CarEntity>

    @Query("SELECT * FROM cars WHERE category = :category ORDER BY created_at DESC")
    suspend fun fetchByCategory(category: String): List<CarEntity>

    @Query("SELECT COUNT(*) FROM cars")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM cars WHERE model = :model")
    suspend fun countByModel(model: String): Int

    @Query("SELECT COUNT(*) FROM cars WHERE year = :year")
    suspend fun countByYear(year: Int): Int

    @Query("SELECT COUNT(*) FROM cars WHERE manufacturer = :manufacturer")
    suspend fun countByManufacturer(manufacturer: String): Int

    @Query("SELECT COUNT(*) FROM cars WHERE brand = :brand")
    suspend fun countByBrand(brand: String): Int

    @Query("SELECT COUNT(*) FROM cars WHERE category = :category")
    suspend fun countByCategory(category: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCar(car: CarEntity)

    @Delete
    suspend fun deleteCar(car: CarEntity): Int
}
