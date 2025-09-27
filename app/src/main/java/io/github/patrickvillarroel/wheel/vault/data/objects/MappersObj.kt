@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class, ExperimentalContracts::class)

package io.github.patrickvillarroel.wheel.vault.data.objects

import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

fun BrandObj.toDomain(image: Any) = Brand(
    name = this.name,
    description = this.description,
    image = image,
    contentDescription = "Logo of $name", // TODO use i18n
    id = this.id!!.toJavaUuid(),
)

inline fun BrandObj.toDomain(image: (BrandObj) -> Any): Brand {
    contract { callsInPlace(image, InvocationKind.EXACTLY_ONCE) }
    return this.toDomain(image(this))
}

fun CarItem.toObject() = CarObj(
    id = this.id.toKotlinUuid(),
    model = this.model,
    year = this.year,
    brand = this.brand,
    manufacturer = this.manufacturer,
    category = this.category,
    description = this.description,
    quantity = this.quantity,
    isFavorite = this.isFavorite,
)

fun CarObj.toDomain(images: Set<Any>) = CarItem(
    id = this.id!!.toJavaUuid(),
    model = this.model,
    year = this.year,
    brand = this.brand,
    manufacturer = this.manufacturer,
    category = this.category,
    description = this.description,
    quantity = this.quantity,
    isFavorite = this.isFavorite,
    images = images,
)

fun VideoObj.toDomain(thumbnail: Any) = VideoNews(
    id = this.id.toJavaUuid(),
    name = this.name,
    link = this.link,
    thumbnail = thumbnail,
    description = this.description,
)
