package io.github.patrickvillarroel.wheel.vault.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.github.patrickvillarroel.wheel.vault.data.entity.CarEntity

@Dao
interface CarDao {
    @Query("SELECT EXISTS(SELECT 1 FROM cars WHERE id = :id)")
    suspend fun exist(id: String): Boolean

    // FIXME use CarFTS entity
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
    suspend fun fetchAllOrderByCreatedDesc(): List<CarEntity>

    @Query("SELECT * FROM cars ORDER BY created_at ASC")
    suspend fun fetchAllOrderByCreatedAsc(): List<CarEntity>

    @Query("SELECT * FROM cars WHERE isFavorite = 1 ORDER BY created_at DESC")
    suspend fun fetchFavoritesOrderByCreatedDesc(): List<CarEntity>

    @Query("SELECT * FROM cars WHERE isFavorite = 1 ORDER BY created_at ASC")
    suspend fun fetchFavoritesOrderByCreatedAsc(): List<CarEntity>

    @Query("SELECT * FROM cars WHERE id = :id LIMIT 1")
    suspend fun fetchById(id: String): CarEntity?

    @Query("SELECT * FROM cars WHERE model = :model ORDER BY created_at DESC")
    suspend fun fetchByModel(model: String): List<CarEntity>

    @Query("SELECT * FROM cars WHERE model = :model AND isFavorite = 1 ORDER BY created_at DESC")
    suspend fun fetchFavoritesByModel(model: String): List<CarEntity>

    @Query("SELECT * FROM cars WHERE year = :year ORDER BY created_at DESC")
    suspend fun fetchByYear(year: Int): List<CarEntity>

    @Query("SELECT * FROM cars WHERE year = :year AND isFavorite = 1 ORDER BY created_at DESC")
    suspend fun fetchFavoritesByYear(year: Int): List<CarEntity>

    @Query("SELECT * FROM cars WHERE manufacturer = :manufacturer ORDER BY created_at DESC")
    suspend fun fetchByManufacturer(manufacturer: String): List<CarEntity>

    @Query("SELECT * FROM cars WHERE manufacturer = :manufacturer AND isFavorite = 1 ORDER BY created_at DESC")
    suspend fun fetchFavoritesByManufacturer(manufacturer: String): List<CarEntity>

    @Query("SELECT * FROM cars WHERE brand = :brand ORDER BY created_at DESC")
    suspend fun fetchByBrand(brand: String): List<CarEntity>

    @Query("SELECT * FROM cars WHERE brand = :brand AND isFavorite = 1 ORDER BY created_at DESC")
    suspend fun fetchFavoritesByBrand(brand: String): List<CarEntity>

    @Query("SELECT * FROM cars WHERE category = :category ORDER BY created_at DESC")
    suspend fun fetchByCategory(category: String): List<CarEntity>

    @Query("SELECT * FROM cars WHERE category = :category AND isFavorite = 1 ORDER BY created_at DESC")
    suspend fun fetchFavoritesByCategory(category: String): List<CarEntity>

    @Query("SELECT COUNT(*) FROM cars")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM cars WHERE isFavorite = 1")
    suspend fun countFavorites(): Int

    @Query("SELECT COUNT(*) FROM cars WHERE model = :model")
    suspend fun countByModel(model: String): Int

    @Query("SELECT COUNT(*) FROM cars WHERE model = :model AND isFavorite = 1")
    suspend fun countFavoritesByModel(model: String): Int

    @Query("SELECT COUNT(*) FROM cars WHERE year = :year")
    suspend fun countByYear(year: Int): Int

    @Query("SELECT COUNT(*) FROM cars WHERE year = :year AND isFavorite = 1")
    suspend fun countFavoritesByYear(year: Int): Int

    @Query("SELECT COUNT(*) FROM cars WHERE manufacturer = :manufacturer")
    suspend fun countByManufacturer(manufacturer: String): Int

    @Query("SELECT COUNT(*) FROM cars WHERE manufacturer = :manufacturer AND isFavorite = 1")
    suspend fun countFavoritesByManufacturer(manufacturer: String): Int

    @Query("SELECT COUNT(*) FROM cars WHERE brand = :brand")
    suspend fun countByBrand(brand: String): Int

    @Query("SELECT COUNT(*) FROM cars WHERE brand = :brand AND isFavorite = 1")
    suspend fun countFavoritesByBrand(brand: String): Int

    @Query("SELECT COUNT(*) FROM cars WHERE category = :category")
    suspend fun countByCategory(category: String): Int

    @Query("SELECT COUNT(*) FROM cars WHERE category = :category AND isFavorite = 1")
    suspend fun countFavoritesByCategory(category: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCar(car: CarEntity)

    @Delete
    suspend fun deleteCar(car: CarEntity): Int
}
