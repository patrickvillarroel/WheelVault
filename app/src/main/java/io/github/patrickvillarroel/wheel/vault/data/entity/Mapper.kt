package io.github.patrickvillarroel.wheel.vault.data.entity
import io.github.patrickvillarroel.wheel.vault.domain.model.*

fun BrandEntity.toDomain(): Brand = Brand(
    id = this.id,
    name = this.name,
    description = this.description,
    image = image,
    contentDescription = this.contentDescription,
)

fun Brand.toEntity(): BrandEntity = BrandEntity(
    id = this.id,
    name = this.name,
    description = this.description,
    image = image,
    contentDescription = this.contentDescription,
)

fun CarEntity.toDomain(): CarItem = CarItem(
    id = this.id,
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

fun CarItem.toEntity(): CarEntity = CarEntity(
    id = this.id,
    model = this.model,
    year = this.year,
    manufacturer = this.manufacturer,
    brand = this.brand,
    images = this.images,
    quantity = this.quantity,
    isFavorite = this.isFavorite,
    description = this.description,
    category = this.category,
)

// VideoNews
fun NewsEntity.toDomain(): VideoNews = VideoNews(
    id = this.id,
    name = this.name,
    link = this.link,
    thumbnail = this.thumbnail,
    description = this.description,
)

fun VideoNews.toEntity(): NewsEntity = NewsEntity(
    id = this.id,
    name = this.name,
    link = this.link,
    thumbnail = this.thumbnail,
    description = this.description,
)
