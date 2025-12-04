package io.github.patrickvillarroel.wheel.vault.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for car images stored locally.
 *
 * This entity stores metadata about images associated with cars.
 * The actual image bytes are stored in the file system via [ImageRepository].
 */
@Entity(
    tableName = "car_images",
    foreignKeys = [
        ForeignKey(
            entity = CarEntity::class,
            parentColumns = ["id_remote"],
            childColumns = ["car_id_remote"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("car_id_remote"),
        Index("id_remote", unique = true),
        Index("is_primary"),
        Index("sync_status"),
    ],
)
class CarImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    /**
     * Foreign key to the car's remote ID (not local id).
     */
    @ColumnInfo(name = "car_id_remote")
    val carIdRemote: String,

    /**
     * Remote ID from Supabase car_images table.
     */
    @ColumnInfo(name = "id_remote")
    val idRemote: String,

    /**
     * Storage path in Supabase storage bucket.
     */
    @ColumnInfo(name = "storage_path")
    val storagePath: String,

    /**
     * MIME type of the image (e.g., "image/png", "image/jpeg").
     */
    @ColumnInfo(name = "mime_type")
    val mimeType: String,

    /**
     * User ID who uploaded this image.
     */
    @ColumnInfo(name = "uploaded_by")
    val uploadedBy: String,

    /**
     * Whether this is the primary/featured image for the car.
     */
    @ColumnInfo(name = "is_primary", defaultValue = "0")
    val isPrimary: Boolean = false,

    /**
     * Local file path where the image is cached.
     * Null if image hasn't been downloaded yet.
     */
    @ColumnInfo(name = "local_path")
    val localPath: String? = null,

    /**
     * Timestamp when this image was created.
     */
    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Long? = null,

    /**
     * Last time this image metadata was modified.
     */
    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP")
    val updatedAt: Long? = null,

    /**
     * Synchronization status with remote server.
     */
    @ColumnInfo(name = "sync_status", defaultValue = "PENDING")
    val syncStatus: SyncStatus = SyncStatus.PENDING,

    /**
     * Last time this image was successfully synced with remote.
     */
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null,

    /**
     * Soft delete flag.
     */
    @ColumnInfo(name = "is_deleted", defaultValue = "0")
    val isDeleted: Boolean = false,
)
