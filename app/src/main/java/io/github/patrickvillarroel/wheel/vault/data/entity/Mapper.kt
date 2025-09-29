package io.github.patrickvillarroel.wheel.vault.data.entity

import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import kotlin.uuid.Uuid

fun BrandEntity.toDomain(image: Any) = Brand(
    id = Uuid.parse(this.id),
    name = this.name,
    description = this.description,
    image = image,
    contentDescription = "Cover image for ${this.name}",
)

fun Brand.toEntity() = BrandEntity(
    id = this.id.toString(),
    name = this.name,
    description = this.description,
)

fun CarEntity.toDomain(images: Set<Any>) = CarItem(
    id = Uuid.parse(idRemote),
    model = this.model,
    year = this.year,
    manufacturer = this.manufacturer,
    brand = brand,
    images = images,
    quantity = this.quantity,
    isFavorite = this.isFavorite,
    description = this.description,
    category = this.category,
)

fun CarItem.toEntity(userId: String?) = CarEntity(
    id = null,
    model = this.model,
    year = this.year,
    manufacturer = this.manufacturer,
    brand = this.brand,
    idRemote = this.id.toString(),
    quantity = this.quantity,
    isFavorite = this.isFavorite,
    description = this.description,
    category = this.category,
    userId = userId,
)

// VideoNews
fun NewsEntity.toDomain(thumbnail: Any) = VideoNews(
    id = Uuid.parse(idRemote),
    name = this.name,
    link = this.link,
    thumbnail = thumbnail,
    description = this.description,
)

fun VideoNews.toEntity(idRemote: String) = NewsEntity(
    id = null,
    name = this.name,
    link = this.link,
    description = this.description,
    idRemote = idRemote,
)
