package io.github.patrickvillarroel.wheel.vault.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.github.patrickvillarroel.wheel.vault.data.entity.CarImageEntity
import io.github.patrickvillarroel.wheel.vault.data.entity.SyncStatus

@Dao
interface CarImageDao {
    /**
     * Fetches all images for a specific car by its remote ID.
     */
    @Query(
        "SELECT * FROM car_images WHERE car_id_remote = :carIdRemote AND is_deleted = 0 ORDER BY is_primary DESC, created_at DESC",
    )
    suspend fun fetchByCarId(carIdRemote: String): List<CarImageEntity>

    /**
     * Fetches the primary image for a car.
     */
    @Query("SELECT * FROM car_images WHERE car_id_remote = :carIdRemote AND is_primary = 1 AND is_deleted = 0 LIMIT 1")
    suspend fun fetchPrimaryImage(carIdRemote: String): CarImageEntity?

    /**
     * Fetches a specific image by its remote ID.
     */
    @Query("SELECT * FROM car_images WHERE id_remote = :idRemote LIMIT 1")
    suspend fun fetchByRemoteId(idRemote: String): CarImageEntity?

    /**
     * Fetches all images that need to be synced (PENDING status).
     */
    @Query("SELECT * FROM car_images WHERE sync_status = 'PENDING' AND is_deleted = 0")
    suspend fun fetchPendingSync(): List<CarImageEntity>

    /**
     * Fetches all images marked for deletion.
     */
    @Query("SELECT * FROM car_images WHERE is_deleted = 1")
    suspend fun fetchDeleted(): List<CarImageEntity>

    /**
     * Fetches images that have not been downloaded yet (local_path is null).
     */
    @Query("SELECT * FROM car_images WHERE local_path IS NULL AND is_deleted = 0 LIMIT :limit")
    suspend fun fetchNotDownloaded(limit: Int = 10): List<CarImageEntity>

    /**
     * Checks if an image exists by remote ID.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM car_images WHERE id_remote = :idRemote)")
    suspend fun exists(idRemote: String): Boolean

    /**
     * Inserts a new image. Replaces if conflict on remote ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: CarImageEntity): Long

    /**
     * Inserts multiple images.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<CarImageEntity>)

    /**
     * Updates an existing image.
     */
    @Update
    suspend fun update(image: CarImageEntity)

    /**
     * Updates the local path after downloading an image.
     */
    @Query("UPDATE car_images SET local_path = :localPath WHERE id_remote = :idRemote")
    suspend fun updateLocalPath(idRemote: String, localPath: String)

    /**
     * Updates the sync status and last synced timestamp.
     */
    @Query(
        "UPDATE car_images SET sync_status = :syncStatus, last_synced_at = :lastSyncedAt WHERE id_remote = :idRemote",
    )
    suspend fun updateSyncStatus(idRemote: String, syncStatus: SyncStatus, lastSyncedAt: Long)

    /**
     * Marks an image as primary and clears primary flag from other images of the same car.
     */
    @Transaction
    suspend fun setPrimaryImage(carIdRemote: String, imageIdRemote: String) {
        clearPrimaryFlags(carIdRemote)
        markAsPrimary(imageIdRemote)
    }

    @Query("UPDATE car_images SET is_primary = 0 WHERE car_id_remote = :carIdRemote")
    suspend fun clearPrimaryFlags(carIdRemote: String)

    @Query("UPDATE car_images SET is_primary = 1 WHERE id_remote = :imageIdRemote")
    suspend fun markAsPrimary(imageIdRemote: String)

    /**
     * Soft deletes an image by marking is_deleted flag.
     */
    @Query("UPDATE car_images SET is_deleted = 1, sync_status = 'PENDING' WHERE id_remote = :idRemote")
    suspend fun softDelete(idRemote: String)

    /**
     * Permanently deletes images marked as deleted after successful sync.
     */
    @Query("DELETE FROM car_images WHERE is_deleted = 1 AND sync_status = 'SYNCED'")
    suspend fun purgeSyncedDeleted()

    /**
     * Deletes all images for a specific car (cascade should handle this automatically).
     */
    @Query("DELETE FROM car_images WHERE car_id_remote = :carIdRemote")
    suspend fun deleteByCarId(carIdRemote: String)

    /**
     * Counts total images for a car.
     */
    @Query("SELECT COUNT(*) FROM car_images WHERE car_id_remote = :carIdRemote AND is_deleted = 0")
    suspend fun countByCarId(carIdRemote: String): Int
}
