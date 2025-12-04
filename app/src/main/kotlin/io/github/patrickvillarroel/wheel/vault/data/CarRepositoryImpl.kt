package io.github.patrickvillarroel.wheel.vault.data

import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.data.dao.CarDao
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.CarRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.CarSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.data.entity.SyncStatus
import io.github.patrickvillarroel.wheel.vault.data.entity.toDomain
import io.github.patrickvillarroel.wheel.vault.data.entity.toEntity
import io.github.patrickvillarroel.wheel.vault.data.mediator.CarOfflineFirstMediator
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.model.Page
import io.github.patrickvillarroel.wheel.vault.domain.model.PagedSource
import io.github.patrickvillarroel.wheel.vault.domain.model.withOfflineFirstMediator
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

/**
 * Repository implementation with offline-first synchronization strategy.
 *
 * This repository coordinates between:
 * - Room (local cache, source of truth)
 * - Supabase (remote source)
 * - SyncMediatorV2 (synchronization logic)
 *
 * Key features:
 * - Offline-first: Always read from Room first
 * - Auto-sync: Syncs pending changes when online
 * - Last Write Wins: Resolves conflicts by timestamp
 * - Pagination with OfflineFirstMediator
 */
class CarRepositoryImpl(
    private val room: CarRoomDataSource,
    private val supabase: CarSupabaseDataSource,
    private val carDao: CarDao,
) : CarsRepository {
    private val logger = Logger.withTag("CarRepositoryImpl")

    override suspend fun exist(id: Uuid): Boolean = room.exist(id)

    override suspend fun search(query: String, isFavorite: Boolean): List<CarItem> = SyncMediator.fetchList(
        forceRefresh = false,
        localFetch = { room.search(query, isFavorite) },
        remoteFetch = { supabase.search(query, isFavorite) },
        saveRemote = { cars ->
            carDao.insertAll(cars.map { it.toEntity(null) })
        },
    )

    override suspend fun fetchAll(isFavorite: Boolean, limit: Int, orderAsc: Boolean): List<CarItem> =
        SyncMediator.fetchList(
            forceRefresh = false,
            localFetch = { room.fetchAll(isFavorite, limit, orderAsc) },
            remoteFetch = { supabase.fetchAll(isFavorite, limit, orderAsc) },
            saveRemote = { cars ->
                carDao.insertAll(cars.map { it.toEntity(null) })
            },
        )

    override suspend fun fetchAllImage(limit: Int, orderAsc: Boolean): Map<Uuid, Any> {
        // Delegate to supabase for now (images sync is handled separately)
        return supabase.fetchAllImage(limit, orderAsc)
    }

    override fun fetchAllImagePaged(orderAsc: Boolean): PagedSource<Int, Pair<Uuid, Any>> {
        // Delegate to supabase for now
        return supabase.fetchAllImagePaged(orderAsc)
    }

    override fun fetchAllPaged(isFavorite: Boolean, orderAsc: Boolean): PagedSource<Int, CarItem> {
        val localSource = createLocalPagedSource(isFavorite, orderAsc)
        val mediator = CarOfflineFirstMediator(supabase)
        return localSource.withOfflineFirstMediator(mediator, localSource)
    }

    override fun fetchPagedWithFilters(
        query: String?,
        manufacturer: String?,
        isFavorite: Boolean,
        orderAsc: Boolean,
    ): PagedSource<Int, CarItem> {
        // For now, delegate to supabase (filtered paging with offline-first is more complex)
        return supabase.fetchPagedWithFilters(query, manufacturer, isFavorite, orderAsc)
    }

    override suspend fun fetch(id: Uuid): CarItem? = SyncMediator.fetch(
        forceRefresh = false,
        localFetch = { room.fetch(id) },
        remoteFetch = { supabase.fetch(id) },
        saveRemote = { car ->
            launch {
                carDao.insertCar(car.toEntity(null))
            }
        },
    )

    override suspend fun fetchByModel(model: String, isFavorite: Boolean): List<CarItem> = SyncMediator.fetchList(
        forceRefresh = false,
        localFetch = { room.fetchByModel(model, isFavorite) },
        remoteFetch = { supabase.fetchByModel(model, isFavorite) },
        saveRemote = { cars ->
            carDao.insertAll(cars.map { it.toEntity(null) })
        },
    )

    override suspend fun fetchByYear(year: Int, isFavorite: Boolean): List<CarItem> = SyncMediator.fetchList(
        forceRefresh = false,
        localFetch = { room.fetchByYear(year, isFavorite) },
        remoteFetch = { supabase.fetchByYear(year, isFavorite) },
        saveRemote = { cars ->
            carDao.insertAll(cars.map { it.toEntity(null) })
        },
    )

    override suspend fun fetchByManufacturer(manufacturer: String, isFavorite: Boolean): List<CarItem> =
        SyncMediator.fetchList(
            forceRefresh = false,
            localFetch = { room.fetchByManufacturer(manufacturer, isFavorite) },
            remoteFetch = { supabase.fetchByManufacturer(manufacturer, isFavorite) },
            saveRemote = { cars ->
                carDao.insertAll(cars.map { it.toEntity(null) })
            },
        )

    override suspend fun fetchByBrand(brand: String, isFavorite: Boolean): List<CarItem> = SyncMediator.fetchList(
        forceRefresh = false,
        localFetch = { room.fetchByBrand(brand, isFavorite) },
        remoteFetch = { supabase.fetchByBrand(brand, isFavorite) },
        saveRemote = { cars ->
            carDao.insertAll(cars.map { it.toEntity(null) })
        },
    )

    override suspend fun fetchByCategory(category: String, isFavorite: Boolean): List<CarItem> = SyncMediator.fetchList(
        forceRefresh = false,
        localFetch = { room.fetchByCategory(category, isFavorite) },
        remoteFetch = { supabase.fetchByCategory(category, isFavorite) },
        saveRemote = { cars ->
            carDao.insertAll(cars.map { it.toEntity(null) })
        },
    )

    override suspend fun count(isFavorite: Boolean): Int = room.count(isFavorite)

    override suspend fun countByModel(model: String, isFavorite: Boolean): Int = room.countByModel(model, isFavorite)

    override suspend fun countByYear(year: Int, isFavorite: Boolean): Int = room.countByYear(year, isFavorite)

    override suspend fun countByManufacturer(manufacturer: String, isFavorite: Boolean): Int =
        room.countByManufacturer(manufacturer, isFavorite)

    override suspend fun countByBrand(brand: String, isFavorite: Boolean): Int = room.countByBrand(brand, isFavorite)

    override suspend fun countByCategory(category: String, isFavorite: Boolean): Int =
        room.countByCategory(category, isFavorite)

    override suspend fun insert(car: CarItem): CarItem {
        val entity = car.toEntity(null)
        carDao.insertCar(entity)

        // Try to sync immediately
        return try {
            val remoteCar = supabase.insert(car)
            carDao.updateSyncStatus(
                idRemote = remoteCar.id.toString(),
                syncStatus = SyncStatus.SYNCED,
                lastSyncedAt = System.currentTimeMillis(),
            )
            logger.v { "Car inserted and synced: ${remoteCar.id}" }
            remoteCar
        } catch (e: Exception) {
            logger.w(e) { "Failed to sync new car immediately, will retry later" }
            car // Return local version with PENDING status
        }
    }

    override suspend fun update(car: CarItem): CarItem {
        val entity = car.toEntity(null)
        carDao.updateCar(entity)

        // Try to sync immediately
        return try {
            val remoteCar = supabase.update(car)
            carDao.updateSyncStatus(
                idRemote = remoteCar.id.toString(),
                syncStatus = SyncStatus.SYNCED,
                lastSyncedAt = System.currentTimeMillis(),
            )
            logger.v { "Car updated and synced: ${remoteCar.id}" }
            remoteCar
        } catch (e: Exception) {
            logger.w(e) { "Failed to sync updated car immediately, will retry later" }
            car
        }
    }

    override suspend fun delete(car: CarItem): Boolean {
        // Soft delete
        carDao.softDelete(car.id.toString())

        // Try to sync deletion immediately
        return try {
            val deleted = supabase.delete(car)
            if (deleted) {
                carDao.updateSyncStatus(
                    idRemote = car.id.toString(),
                    syncStatus = SyncStatus.SYNCED,
                    lastSyncedAt = System.currentTimeMillis(),
                )
                // Purge after successful sync
                carDao.purgeSyncedDeleted()
                logger.v { "Car deleted and synced: ${car.id}" }
            }
            deleted
        } catch (e: Exception) {
            logger.w(e) { "Failed to sync deletion immediately, will retry later" }
            true // Still returns true (soft deleted locally)
        }
    }

    override suspend fun setAvailableForTrade(carId: Uuid, isAvailable: Boolean): CarItem? =
        supabase.setAvailableForTrade(carId, isAvailable)

    /**
     * Creates a paged source that reads from local Room database.
     */
    private fun createLocalPagedSource(isFavorite: Boolean, orderAsc: Boolean): PagedSource<Int, CarItem> =
        PagedSource { key, size ->
            val page = key ?: 0
            val offset = page * size
            val entities = if (isFavorite) {
                if (orderAsc) carDao.fetchFavoritesOrderByCreatedAsc() else carDao.fetchFavoritesOrderByCreatedDesc()
            } else {
                if (orderAsc) carDao.fetchAllOrderByCreatedAsc() else carDao.fetchAllOrderByCreatedDesc()
            }

            val pageEntities = entities.drop(offset).take(size)
            val cars = pageEntities.map { it.toDomain(setOf(CarItem.EmptyImage)) }

            Page(
                data = cars,
                prevKey = if (page > 0) page - 1 else null,
                nextKey = if (pageEntities.size == size) page + 1 else null,
            )
        }
}
