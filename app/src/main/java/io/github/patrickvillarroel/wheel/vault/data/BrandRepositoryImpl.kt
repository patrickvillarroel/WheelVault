@file:OptIn(ExperimentalUuidApi::class)

package io.github.patrickvillarroel.wheel.vault.data

import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageDownloadHelper
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.BrandRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.BrandSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

/**
 * Brands repository.
 *
 * This handle between data-sources, current only delegate to supabase.
 */
class BrandRepositoryImpl(
    private val supabase: BrandSupabaseDataSource,
    private val room: BrandRoomDataSource,
    private val imageHelper: ImageDownloadHelper,
) : BrandRepository {
    override suspend fun search(query: String): List<Brand> = supabase.search(query)

    override suspend fun fetchAll(forceRefresh: Boolean): List<Brand> = SyncMediator.fetchList(
        localFetch = { room.fetchAll(forceRefresh) },
        remoteFetch = { supabase.fetchAll(forceRefresh) },
        saveRemote = {
            coroutineScope {
                room.saveAll(
                    it,
                    it.map {
                        async {
                            imageHelper.downloadImage(supabase.buildImageRequest(it.id.toKotlinUuid()))
                        }
                    }
                        .awaitAll()
                        .filterNotNull(),
                )
            }
        },
        forceRefresh = forceRefresh,
    )

    override suspend fun fetch(id: UUID): Brand? = supabase.fetch(id)

    override suspend fun fetchByName(name: String): Brand? = supabase.fetchByName(name)

    override suspend fun fetchByDescription(description: String): Brand? = supabase.fetchByDescription(description)
}
