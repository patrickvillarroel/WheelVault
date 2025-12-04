package io.github.patrickvillarroel.wheel.vault.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "cars",
    indices = [
        Index("model"),
        Index("brand"),
        Index("category"),
        Index("year"),
        Index("id_remote", unique = true),
        Index("sync_status"),
        Index("last_synced_at"),
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

    /**
     * Remote ID from Supabase. This is the source of truth for synchronization.
     * Should be set once when entity is created and never changed.
     */
    @ColumnInfo(name = "id_remote")
    val idRemote: String,

    /**
     * Last time this entity was modified (locally or from remote).
     * Used for Last Write Wins (LWW) conflict resolution.
     */
    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP")
    val updatedAt: Long? = null,

    /**
     * Synchronization status with remote server.
     */
    @ColumnInfo(name = "sync_status", defaultValue = "PENDING")
    val syncStatus: SyncStatus = SyncStatus.PENDING,

    /**
     * Last time this entity was successfully synced with remote.
     * Used to determine if cached data is stale.
     */
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null,

    /**
     * Soft delete flag. If true, entity should be deleted from remote on next sync.
     */
    @ColumnInfo(name = "is_deleted", defaultValue = "0")
    val isDeleted: Boolean = false,
)
