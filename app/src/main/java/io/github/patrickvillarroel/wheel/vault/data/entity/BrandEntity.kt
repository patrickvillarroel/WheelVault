package io.github.patrickvillarroel.wheel.vault.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(indices = [Index("name", unique = true), Index("id_remote", unique = true)])
data class BrandEntity(
    @PrimaryKey(true)
    val id: Long? = null,
    val name: String,
    val description: String,
    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP", index = true)
    val createdAt: Long? = null,
    @ColumnInfo("id_remote")
    val idRemote: String = UUID.randomUUID().toString(),
)
