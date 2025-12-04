package io.github.patrickvillarroel.wheel.vault.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "brands",
    indices = [
        Index("sync_status"),
        Index("last_synced_at"),
    ],
)
class BrandEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Long? = null,

    /**
     * Last time this entity was modified (locally or from remote).
     * Used for Last Write Wins (LWW) conflict resolution.
     */
    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP")
    val updatedAt: Long? = null,

    /**
     * Synchronization status with remote server.
     */
    @ColumnInfo(name = "sync_status", defaultValue = "SYNCED")
    val syncStatus: SyncStatus = SyncStatus.SYNCED,

    /**
     * Last time this entity was successfully synced with remote.
     * Used to determine if cached data is stale.
     */
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null,

    /**
     * Soft delete flag.
     */
    @ColumnInfo(name = "is_deleted", defaultValue = "0")
    val isDeleted: Boolean = false,
)
