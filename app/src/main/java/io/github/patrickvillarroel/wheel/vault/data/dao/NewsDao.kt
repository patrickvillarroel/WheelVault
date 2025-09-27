package io.github.patrickvillarroel.wheel.vault.data.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.patrickvillarroel.wheel.vault.data.entity.NewsEntity

@Dao
interface NewsDao {
    @Query("SELECT * FROM news WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<NewsEntity>

    @Query("SELECT * FROM news ORDER BY created_at DESC")
    suspend fun fetchAll(): List<NewsEntity>

    @Query("SELECT * FROM news WHERE id = :id LIMIT 1")
    suspend fun fetch(id: String): NewsEntity?

    @Query("SELECT * FROM news WHERE link = :link LIMIT 1")
    suspend fun fetchByLink(link: String): NewsEntity?

    @Query("SELECT COUNT(*) FROM news")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity)

    @Query("DELETE FROM news WHERE id = :id")
    suspend fun deleteById(id: String)
}
