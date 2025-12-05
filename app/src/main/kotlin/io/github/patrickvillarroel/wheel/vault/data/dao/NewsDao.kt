package io.github.patrickvillarroel.wheel.vault.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.patrickvillarroel.wheel.vault.data.entity.NewsEntity

@Dao
interface NewsDao {
    @Query("SELECT * FROM news ORDER BY created_at DESC")
    suspend fun fetchAll(): List<NewsEntity>

    @Query("SELECT * FROM news ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    suspend fun fetchAll(limit: Int, offset: Int): List<NewsEntity>

    @Query("SELECT * FROM news WHERE id = :id LIMIT 1")
    suspend fun fetch(id: String): NewsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNews(news: List<NewsEntity>)

    @Query("DELETE FROM news WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM news")
    suspend fun deleteAll()
}
