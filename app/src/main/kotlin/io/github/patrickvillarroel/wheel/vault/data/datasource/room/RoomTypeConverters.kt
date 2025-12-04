package io.github.patrickvillarroel.wheel.vault.data.datasource.room

import androidx.room.TypeConverter
import io.github.patrickvillarroel.wheel.vault.data.entity.SyncStatus

/**
 * Type converters for Room database.
 *
 * These converters allow Room to persist custom types that are not natively supported.
 */
class RoomTypeConverters {
    /**
     * Converts [SyncStatus] enum to String for database storage.
     */
    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    /**
     * Converts String from database to [SyncStatus] enum.
     */
    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = try {
        SyncStatus.valueOf(value)
    } catch (e: IllegalArgumentException) {
        // Fallback to PENDING if invalid value
        SyncStatus.PENDING
    }
}
