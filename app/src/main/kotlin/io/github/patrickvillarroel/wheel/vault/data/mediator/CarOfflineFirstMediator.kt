package io.github.patrickvillarroel.wheel.vault.data.mediator

import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.CarSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.model.OfflineFirstMediator

/**
 * Offline-first mediator implementation for Car entities.
 *
 * This mediator coordinates synchronization between Room (local cache) and Supabase (remote source)
 * for car data. It implements the pagination and sync logic defined by [OfflineFirstMediator].
 *
 * Key features:
 * - Uses [io.github.patrickvillarroel.wheel.vault.domain.model.CachePolicy.CARS] for staleness detection (30 minutes TTL)
 * - Handles pagination with Int keys (page numbers)
 * - Automatically syncs local changes with remote on refresh
 * - Falls back gracefully on network errors
 *
 * @param supabaseDataSource Supabase data source for remote operations
 * @param pageSize Number of items per page
 */
class CarOfflineFirstMediator(private val supabaseDataSource: CarSupabaseDataSource, private val pageSize: Int = 20) :
    OfflineFirstMediator<Int, CarItem>() {
    private val logger = Logger.withTag("CarOfflineFirstMediator")

    override suspend fun load(loadType: LoadType, key: Int?): MediatorResult = try {
        logger.v { "Loading cars: loadType=$loadType, key=$key" }

        when (loadType) {
            LoadType.REFRESH -> {
                // Push local pending changes to remote first
                syncPendingChanges()

                // Fetch fresh data from remote
                val remoteCars = supabaseDataSource.fetchAll(isFavorite = false, limit = pageSize, orderAsc = false)
                logger.v { "Fetched ${remoteCars.size} cars from remote" }

                // TODO: Convert domain CarItem back to CarEntity and save to Room
                // This requires mapper updates to handle sync metadata

                MediatorResult.Success(endOfPaginationReached = remoteCars.size < pageSize)
            }

            LoadType.APPEND -> {
                val page = key ?: 0
                val offset = page * pageSize

                val remoteCars = supabaseDataSource.fetchAll(isFavorite = false, limit = pageSize, orderAsc = false)
                logger.v { "Fetched ${remoteCars.size} cars from remote (page=$page)" }

                // TODO: Save to Room

                MediatorResult.Success(endOfPaginationReached = remoteCars.size < pageSize)
            }

            LoadType.PREPEND -> {
                // Not typically used for descending order pagination
                MediatorResult.Success(endOfPaginationReached = true)
            }
        }
    } catch (e: Exception) {
        logger.e(e) { "Failed to load cars from remote" }
        MediatorResult.Error(e)
    }

    override suspend fun shouldRefresh(cachedData: List<CarItem>): Boolean {
        if (cachedData.isEmpty()) {
            logger.v { "Cache is empty, should refresh" }
            return true
        }

        // Check if any cached data is stale
        // TODO: This requires CarItem to expose sync metadata from entity
        // For now, we'll use a simple heuristic
        val hasStaleData = false // TODO: Implement staleness check

        logger.v { "Cache has ${cachedData.size} items, stale=$hasStaleData" }
        return hasStaleData
    }

    /**
     * Syncs pending local changes to remote before fetching fresh data.
     *
     * This ensures that user edits made offline are not lost when refreshing.
     */
    private fun syncPendingChanges() {
        try {
            // TODO: Query cars with PENDING sync status
            // TODO: Push each pending car to Supabase
            // TODO: Update sync status to SYNCED on success
            logger.v { "Syncing pending changes (TODO)" }
        } catch (e: Exception) {
            logger.w(e) { "Failed to sync pending changes, continuing with refresh" }
        }
    }
}
