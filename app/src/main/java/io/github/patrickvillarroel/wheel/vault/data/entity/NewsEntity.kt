package io.github.patrickvillarroel.wheel.vault.data.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String?,
    val link: String,
    val created_at: Long = System.currentTimeMillis(),
)
