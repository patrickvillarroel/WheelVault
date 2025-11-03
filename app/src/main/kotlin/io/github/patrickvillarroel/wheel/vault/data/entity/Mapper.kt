package io.github.patrickvillarroel.wheel.vault.data.entity

import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import kotlin.time.Instant
import kotlin.uuid.Uuid

fun BrandEntity.toDomain(image: Any) = Brand(
    id = Uuid.parse(this.id),
    name = this.name,
    description = this.description,
    image = image,
    contentDescription = "Logo of ${this.name}",
    createdAt = this.createdAt?.let { Instant.fromEpochMilliseconds(it) },
)

fun Brand.toEntity() = BrandEntity(
    id = this.id.toString(),
    name = this.name,
    description = this.description,
    createdAt = this.createdAt?.toEpochMilliseconds(),
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
