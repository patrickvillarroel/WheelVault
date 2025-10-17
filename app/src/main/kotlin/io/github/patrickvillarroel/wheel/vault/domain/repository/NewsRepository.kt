package io.github.patrickvillarroel.wheel.vault.domain.repository

import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import kotlin.uuid.Uuid

// TODO este repository no esta probado ni usado en los VM correspondientes
interface NewsRepository {
    suspend fun search(query: String): List<VideoNews>
    suspend fun fetchAll(): List<VideoNews>
    suspend fun fetch(id: Uuid): VideoNews?
    suspend fun fetchByLink(link: String): VideoNews?
    suspend fun fetchByTitle(title: String): VideoNews?
}
