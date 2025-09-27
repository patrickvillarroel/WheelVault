package io.github.patrickvillarroel.wheel.vault.data.datasource
import io.github.patrickvillarroel.wheel.vault.data.dao.NewsDao
import io.github.patrickvillarroel.wheel.vault.data.entity.NewsEntity
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.domain.repository.NewsRepository
import java.util.UUID
import javax.inject.Inject

data class NewsRoomDataSource(private val dao: NewsDao) : NewsRepository {

    override suspend fun search(query: String): List<VideoNews> {
        dao.search(query)
    }

    override suspend fun fetchAll(): List<VideoNews> {
        dao.fetchAll()
    }

    override suspend fun fetch(id: UUID): VideoNews? {
        TODO("Not yet implemented")
    }

    override suspend fun fetchByLink(link: String): VideoNews? {
        dao.fetchByLink(link)
    }

    override suspend fun fetchByTitle(title: String): VideoNews? {
        TODO("Not yet implemented")
    }

    suspend fun count(): Int = dao.count()

    suspend fun insert(news: VideoNews) {
        dao.insertNews()
    }

    suspend fun delete(id: String) {
        dao.deleteById(id)
    }
}
