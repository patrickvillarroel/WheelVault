package io.github.patrickvillarroel.wheel.vault.domain.repository

import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.PagedSource
import kotlin.uuid.Uuid

/** Repository for cars brands. */
interface BrandRepository {
    suspend fun search(query: String): List<Brand>
    suspend fun fetchAllNames(forceRefresh: Boolean = false): List<String>
    suspend fun fetchAllImages(forceRefresh: Boolean = false): Map<Uuid, Any>
    fun fetchAllImagesPaged(): PagedSource<Int, Pair<Uuid, Any>>
    suspend fun fetchAll(forceRefresh: Boolean = false): List<Brand>
    suspend fun fetch(id: Uuid, forceRefresh: Boolean = false): Brand?
    suspend fun fetchByName(name: String): Brand?
    suspend fun fetchByDescription(description: String): Brand?
}
