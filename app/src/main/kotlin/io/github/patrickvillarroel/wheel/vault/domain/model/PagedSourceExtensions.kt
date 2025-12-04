package io.github.patrickvillarroel.wheel.vault.domain.model

import co.touchlab.kermit.Logger

/**
 * Creates a new [PagedSource] that uses [OfflineFirstMediator] for data synchronization.
 *
 * This wraps the original PagedSource and coordinates with the mediator to:
 * 1. Check if cached data is stale before loading
 * 2. Trigger remote fetch via mediator when needed
 * 3. Return data from local cache after sync
 *
 * @param mediator The mediator responsible for remote sync logic
 * @param localSource The local data source (typically Room database)
 * @return A new PagedSource that handles offline-first loading
 */
fun <Key : Any, Value : Any> PagedSource<Key, Value>.withOfflineFirstMediator(
    mediator: OfflineFirstMediator<Key, Value>,
    localSource: PagedSource<Key, Value>,
): PagedSource<Key, Value> = OfflineFirstPagedSource(mediator, localSource)

/**
 * Internal implementation of PagedSource with OfflineFirstMediator integration.
 */
private class OfflineFirstPagedSource<Key : Any, Value : Any>(
    private val mediator: OfflineFirstMediator<Key, Value>,
    private val localSource: PagedSource<Key, Value>,
) : PagedSource<Key, Value> {
    private val logger = Logger.withTag("OfflineFirstPagedSource")

    override suspend fun loadPage(key: Key?, size: Int): Page<Key, Value> {
        // First, load from local cache
        val localPage = try {
            localSource.loadPage(key, size)
        } catch (e: Exception) {
            logger.e(e) { "Failed to load from local source" }
            return Page(data = emptyList(), prevKey = null, nextKey = null)
        }

        // Determine if we need to sync with remote
        val loadType = when {
            key == null -> OfflineFirstMediator.LoadType.REFRESH
            else -> OfflineFirstMediator.LoadType.APPEND
        }

        // Check if we should refresh from remote
        val shouldRefresh = try {
            mediator.shouldRefresh(localPage.data)
        } catch (e: Exception) {
            logger.w(e) { "shouldRefresh check failed, skipping remote sync" }
            false
        }

        if (shouldRefresh || (key == null && localPage.data.isEmpty())) {
            // Trigger remote sync via mediator
            logger.v { "Triggering remote sync: loadType=$loadType, key=$key" }
            when (val result = mediator.load(loadType, key)) {
                is OfflineFirstMediator.MediatorResult.Success -> {
                    logger.v { "Remote sync succeeded, endOfPagination=${result.endOfPaginationReached}" }
                    // Reload from local source after sync
                    return try {
                        localSource.loadPage(key, size)
                    } catch (e: Exception) {
                        logger.e(e) { "Failed to reload from local source after sync" }
                        localPage
                    }
                }

                is OfflineFirstMediator.MediatorResult.Error -> {
                    logger.e(result.error) { "Remote sync failed, returning cached data" }
                    // Return cached data even if sync failed (offline-first)
                    return localPage
                }
            }
        }

        // Return cached data (no sync needed)
        return localPage
    }
}
