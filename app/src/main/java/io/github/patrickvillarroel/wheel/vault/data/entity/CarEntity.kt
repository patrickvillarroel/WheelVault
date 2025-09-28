package io.github.patrickvillarroel.wheel.vault.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "cars",
    indices = [
        Index("model"),
        Index("brand"),
        Index("category"),
        Index("year"),
        Index("id_remote", unique = true),
    ],
)
class CarEntity(
    @PrimaryKey(true)
    val id: Long? = null,
    val model: String,

    val year: Int,
    val brand: String,
    val manufacturer: String,
    val category: String? = null,

    val description: String? = null,
    val quantity: Int = 0,
    @ColumnInfo(name = "isFavorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP", index = true)
    val createdAt: Long? = null,

    @ColumnInfo(name = "user_id")
    val userId: String? = null,

    // FIXME
    @ColumnInfo(name = "id_remote")
    val idRemote: String = UUID.randomUUID().toString(),
)
