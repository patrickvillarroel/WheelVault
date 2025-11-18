package io.github.patrickvillarroel.wheel.vault.data.datasource.room

import io.github.patrickvillarroel.wheel.vault.data.dao.NewsDao
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageRepository
import io.github.patrickvillarroel.wheel.vault.data.entity.NewsEntity
import io.github.patrickvillarroel.wheel.vault.domain.model.Page
import io.github.patrickvillarroel.wheel.vault.domain.model.PagedSource
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import kotlin.time.Instant
import kotlin.uuid.Uuid

class GetVideoNewsRoomDataSource(private val dao: NewsDao, private val imageRepository: ImageRepository) :
    GetVideosNewsUseCase {
    override suspend fun getVideos(forceRefresh: Boolean): List<VideoNews> =
        dao.fetchAll().map { it.toDomain(imageRepository.loadImage(it.id + ".png") ?: VideoNews.DEFAULT_IMAGE) }

    override fun getVideosPaged(): PagedSource<Int, VideoNews> = PagedSource { key, size ->
        val offset = key ?: 0

        val data = dao.fetchAll(offset = offset, limit = size).map { v ->
            v.toDomain(imageRepository.loadImage(v.id + ".png") ?: VideoNews.DEFAULT_IMAGE)
        }

        val nextKey = if (data.size < size) null else offset + size
        val prevKey = if (offset == 0) null else maxOf(offset - size, 0)

        Page(data = data, prevKey = prevKey, nextKey = nextKey)
    }

    suspend fun save(videos: List<VideoNews>, images: Map<Uuid, ByteArray>) {
        dao.insertAllNews(videos.map { it.toEntity() })
        images.forEach { (id, imageByte) -> imageRepository.saveImage("$id.png", imageByte) }
    }

    companion object {
        private fun NewsEntity.toDomain(thumbnail: Any) = VideoNews(
            id = Uuid.parse(id),
            name = this.name,
            link = this.link,
            thumbnail = thumbnail,
            description = this.description,
            createdAt = this.createdAt?.let { Instant.fromEpochMilliseconds(it) },
        )

        private fun VideoNews.toEntity() = NewsEntity(
            id = id.toString(),
            name = this.name,
            link = this.link,
            description = this.description,
            createdAt = this.createdAt?.toEpochMilliseconds(),
        )
    }
}
