package io.github.patrickvillarroel.wheel.vault.data

import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageDownloadHelper
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageRepository
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.BrandRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.BrandSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.PagedSource
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
    private val imageRepository: ImageRepository,
) : BrandRepository {
    // TODO brand FTS is not ready yet
    override suspend fun search(query: String): List<Brand> = supabase.search(query)

    override suspend fun fetchAllNames(forceRefresh: Boolean): List<String> = SyncMediator.fetchList(
        forceRefresh = forceRefresh,
        localFetch = { room.fetchAllNames() },
        remoteFetch = { supabase.fetchAllNames(forceRefresh) },
        saveRemote = { _ ->
            // Names are derived from brands, so sync brands instead
        },
    )

    override suspend fun fetchAllImages(forceRefresh: Boolean): Map<Uuid, Any> = SyncMediator.fetchMap(
        forceRefresh = forceRefresh,
        localFetch = { room.fetchAllImages(forceRefresh) },
        remoteFetch = { supabase.fetchAllImages(forceRefresh) },
        saveRemote = { remoteImages ->
            launch {
                remoteImages.forEach { (id) ->
                    imageHelper.downloadImage(supabase.buildImageRequest(id))?.let { imageBytes ->
                        imageRepository.saveImage("$id.png", imageBytes)
                    }
                }
            }
        },
    )

    // TODO brands images is not ready yet
    override fun fetchAllImagesPaged(): PagedSource<Int, Pair<Uuid, Any>> = supabase.fetchAllImagesPaged()

    override suspend fun fetchAll(forceRefresh: Boolean): List<Brand> = SyncMediator.fetchList(
        localFetch = { room.fetchAll(forceRefresh) },
        remoteFetch = { supabase.fetchAll(forceRefresh) },
        saveRemote = { brands ->
            launch {
                room.saveAll(
                    brands,
                    brands.map { brand ->
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

    override suspend fun fetch(id: Uuid, forceRefresh: Boolean): Brand? = SyncMediator.fetch(
        forceRefresh = forceRefresh,
        localFetch = { room.fetch(id, forceRefresh) },
        remoteFetch = { supabase.fetch(id, forceRefresh) },
        saveRemote = { brand ->
            launch { room.save(brand, imageHelper.downloadImage(supabase.buildImageRequest(brand.id))) }
        },
    )

    override suspend fun fetchByName(name: String): Brand? = SyncMediator.fetch(
        forceRefresh = false,
        localFetch = { room.fetchByName(name) },
        remoteFetch = { supabase.fetchByName(name) },
        saveRemote = { brand ->
            launch { room.save(brand, imageHelper.downloadImage(supabase.buildImageRequest(brand.id))) }
        },
    )

    override suspend fun fetchByDescription(description: String): Brand? = SyncMediator.fetch(
        forceRefresh = false,
        localFetch = { room.fetchByDescription(description) },
        remoteFetch = { supabase.fetchByDescription(description) },
        saveRemote = { brand ->
            launch { room.save(brand, imageHelper.downloadImage(supabase.buildImageRequest(brand.id))) }
        },
    )
}
