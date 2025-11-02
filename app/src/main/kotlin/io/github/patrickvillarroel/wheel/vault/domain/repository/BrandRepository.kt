package io.github.patrickvillarroel.wheel.vault.domain.repository

import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import kotlin.uuid.Uuid

/** Repository for cars brands. */
interface BrandRepository {
    suspend fun search(query: String): List<Brand>
    suspend fun fetchAllNames(forceRefresh: Boolean = false): List<String>
    suspend fun fetchAll(forceRefresh: Boolean = false): List<Brand>
    suspend fun fetch(id: Uuid): Brand?
    suspend fun fetchByName(name: String): Brand?
    suspend fun fetchByDescription(description: String): Brand?
}
