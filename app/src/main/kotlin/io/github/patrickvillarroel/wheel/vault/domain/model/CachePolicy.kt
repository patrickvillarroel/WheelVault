package io.github.patrickvillarroel.wheel.vault.domain.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Defines cache time-to-live (TTL) policies for different entity types.
 *
 * This allows configurable staleness detection and automatic refresh from remote source.
 */
data class CachePolicy(val ttl: Duration, val name: String) {
    /**
     * Checks if the cached data is stale based on the last sync timestamp.
     *
     * @param lastSyncedAt The timestamp (in milliseconds) when data was last synced.
     * @return true if data is stale and should be refreshed, false otherwise.
     */
    fun isStale(lastSyncedAt: Long?): Boolean {
        if (lastSyncedAt == null) return true
        val now = System.currentTimeMillis()
        val elapsed = now - lastSyncedAt
        return elapsed > ttl.inWholeMilliseconds
    }

    companion object {
        /**
         * Cache policy for Car entities: 30 minutes TTL.
         * Cars change frequently as users add/edit their collection.
         */
        val CARS = CachePolicy(ttl = 30.minutes, name = "Cars")

        /**
         * Cache policy for Brand entities: 24 hours TTL.
         * Brands are relatively static master data.
         */
        val BRANDS = CachePolicy(ttl = 24.hours, name = "Brands")

        /**
         * Cache policy for News/Video entities: 6 hours TTL.
         * News updates periodically but not as frequently as user data.
         */
        val NEWS = CachePolicy(ttl = 6.hours, name = "News")

        /**
         * Cache policy for Images: 7 days TTL.
         * Images rarely change and are large, so we keep them longer.
         */
        val IMAGES = CachePolicy(ttl = Duration.parse("7d"), name = "Images")
    }
}
