package io.github.patrickvillarroel.wheel.vault.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(tableName = "brands")
class BrandEntity(
    @PrimaryKey val id: String = Uuid.random().toString(),
    val name: String,
    val description: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)
