package io.github.patrickvillarroel.wheel.vault.data.entity

/**
 * Represents the synchronization status of an entity.
 *
 * This is used to track whether local data has been synchronized with the remote server.
 */
enum class SyncStatus {
    /**
     * The entity is fully synchronized with the remote server.
     */
    SYNCED,

    /**
     * The entity has local changes that need to be pushed to the remote server.
     */
    PENDING,

    /**
     * The entity has a conflict between local and remote data.
     * This requires resolution (e.g., Last Write Wins strategy).
     */
    CONFLICT,

    /**
     * The entity has been marked for deletion locally or remotely.
     * Used for soft delete strategy.
     */
    DELETED,
}
