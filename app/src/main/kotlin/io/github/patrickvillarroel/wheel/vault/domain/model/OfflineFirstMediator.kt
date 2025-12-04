package io.github.patrickvillarroel.wheel.vault.domain.model

/**
 * Mediator for offline-first data synchronization with remote source.
 *
 * This abstraction is inspired by AndroidX Paging3's RemoteMediator, but lives in the
 * domain layer to maintain clean architecture boundaries. It coordinates between local
 * cache (Room) and remote source (Supabase).
 *
 * The mediator is responsible for:
 * - Determining when to fetch from remote
 * - Loading data from remote and saving to local cache
 * - Handling synchronization errors
 * - Deciding if cached data is stale
 *
 * @param Key The type of key used for pagination (e.g., Int for page numbers)
 * @param Value The type of domain model being synchronized
 */
abstract class OfflineFirstMediator<Key : Any, Value : Any> {
    /**
     * Loads data from remote source and updates local cache.
     *
     * This method is called when:
     * - Initial load is needed and cache is empty or stale
     * - User triggers a manual refresh (pull-to-refresh)
     * - Pagination needs more data from remote
     *
     * @param loadType The type of load operation to perform
     * @param key The pagination key, or null for initial load
     * @return Result of the load operation
     */
    abstract suspend fun load(loadType: LoadType, key: Key?): MediatorResult

    /**
     * Determines whether cached data should be refreshed from remote.
     *
     * This is called to check if the local cache is stale and needs updating.
     * Typically checks timestamps against [CachePolicy] TTL.
     *
     * @param cachedData The currently cached data in local storage
     * @return true if data should be refreshed from remote, false otherwise
     */
    abstract suspend fun shouldRefresh(cachedData: List<Value>): Boolean

    /**
     * Type of load operation being performed.
     */
    enum class LoadType {
        /**
         * Initial load or refresh of data.
         * Used when cache is empty or manual refresh is triggered.
         */
        REFRESH,

        /**
         * Load data before the current dataset (prepend).
         * Used for bidirectional pagination.
         */
        PREPEND,

        /**
         * Load data after the current dataset (append).
         * Used for forward pagination (most common).
         */
        APPEND,
    }

    /**
     * Result of a mediator load operation.
     */
    sealed class MediatorResult {
        /**
         * Load completed successfully.
         *
         * @param endOfPaginationReached true if there are no more pages to load in this direction
         */
        data class Success(val endOfPaginationReached: Boolean) : MediatorResult()

        /**
         * Load failed with an error.
         *
         * @param error The exception that caused the failure
         */
        data class Error(val error: Throwable) : MediatorResult()
    }
}
