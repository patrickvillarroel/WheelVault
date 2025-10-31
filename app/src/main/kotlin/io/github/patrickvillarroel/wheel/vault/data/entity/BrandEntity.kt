package io.github.patrickvillarroel.wheel.vault.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brands")
class BrandEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP", index = true)
    val createdAt: Long? = null,
)
