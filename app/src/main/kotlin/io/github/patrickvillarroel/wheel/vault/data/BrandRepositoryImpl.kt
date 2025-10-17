package io.github.patrickvillarroel.wheel.vault.data

import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageDownloadHelper
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.BrandRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.BrandSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

/**
 * Brands repository.
 *
 * This handle requests between data-sources.
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
            launch {
                room.saveAll(
                    it,
                    it.map { brand ->
                        async {
                            imageHelper.downloadImage(supabase.buildImageRequest(brand.id))
                        }
                    }
                        .awaitAll()
                        .filterNotNull(),
                )
            }
        },
        forceRefresh = forceRefresh,
    )

    override suspend fun fetch(id: Uuid): Brand? = supabase.fetch(id)

    override suspend fun fetchByName(name: String): Brand? = supabase.fetchByName(name)

    override suspend fun fetchByDescription(description: String): Brand? = supabase.fetchByDescription(description)
}
