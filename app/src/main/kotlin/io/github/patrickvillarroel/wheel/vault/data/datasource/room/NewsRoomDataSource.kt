package io.github.patrickvillarroel.wheel.vault.data.datasource.room

import io.github.patrickvillarroel.wheel.vault.data.dao.NewsDao
import io.github.patrickvillarroel.wheel.vault.data.entity.toDomain
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.domain.repository.NewsRepository
import kotlin.uuid.Uuid

// TODO add image repository loading
class NewsRoomDataSource(private val dao: NewsDao) : NewsRepository {
    override suspend fun search(query: String): List<VideoNews> =
        dao.search(query).map { it.toDomain(VideoNews.DEFAULT_IMAGE) }

    override suspend fun fetchAll(): List<VideoNews> = dao.fetchAll().map { it.toDomain(VideoNews.DEFAULT_IMAGE) }

    // TODO fix this method
    override suspend fun fetch(id: Uuid): VideoNews? = dao.fetch(id.toString())?.toDomain(VideoNews.DEFAULT_IMAGE)

    override suspend fun fetchByLink(link: String): VideoNews? =
        dao.fetchByLink(link)?.toDomain(VideoNews.DEFAULT_IMAGE)

    override suspend fun fetchByTitle(title: String): VideoNews? =
        dao.fetchByName(title)?.toDomain(VideoNews.DEFAULT_IMAGE)
}
