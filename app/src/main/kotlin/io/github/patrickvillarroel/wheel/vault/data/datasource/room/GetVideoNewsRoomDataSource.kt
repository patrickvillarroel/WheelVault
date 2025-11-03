package io.github.patrickvillarroel.wheel.vault.data.datasource.room

import io.github.patrickvillarroel.wheel.vault.data.dao.NewsDao
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageRepository
import io.github.patrickvillarroel.wheel.vault.data.entity.NewsEntity
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import kotlin.time.Instant
import kotlin.uuid.Uuid

class GetVideoNewsRoomDataSource(private val dao: NewsDao, private val imageRepository: ImageRepository) :
    GetVideosNewsUseCase {
    override suspend fun getVideos(forceRefresh: Boolean): List<VideoNews> =
        dao.fetchAll().map { it.toDomain(imageRepository.loadImage(it.id + ".png") ?: VideoNews.DEFAULT_IMAGE) }

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
