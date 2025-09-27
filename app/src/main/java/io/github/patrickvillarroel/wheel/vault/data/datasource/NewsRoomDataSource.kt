package io.github.patrickvillarroel.wheel.vault.data.datasource

import io.github.patrickvillarroel.wheel.vault.data.dao.NewsDao
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.domain.repository.NewsRepository
import java.util.UUID

class NewsRoomDataSource(private val dao: NewsDao) : NewsRepository {
    override suspend fun search(query: String): List<VideoNews> {
        TODO()
    }

    override suspend fun fetchAll(): List<VideoNews> {
        TODO()
    }

    override suspend fun fetch(id: UUID): VideoNews? {
        TODO()
    }

    override suspend fun fetchByLink(link: String): VideoNews? {
        TODO()
    }

    override suspend fun fetchByTitle(title: String): VideoNews? {
        TODO()
    }

    suspend fun count(): Int = dao.count()

    suspend fun insert(news: VideoNews) {
        TODO()
    }

    suspend fun delete(id: String) {
        dao.deleteById(id)
    }
}
