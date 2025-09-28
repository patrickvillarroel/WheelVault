package io.github.patrickvillarroel.wheel.vault.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
class NewsEntity(
    @PrimaryKey
    val id: Long? = null,
    val name: String,
    val description: String?,
    val link: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long? = null,
    @ColumnInfo(name = "id_remote")
    val idRemote: String,
)
