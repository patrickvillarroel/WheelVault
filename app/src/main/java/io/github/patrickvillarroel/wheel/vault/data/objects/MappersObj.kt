@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class, ExperimentalContracts::class)

package io.github.patrickvillarroel.wheel.vault.data.objects

import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

context(_: BrandRepository)
fun BrandObj.toDomain(image: Any) = Brand(
    name = this.name,
    description = this.description,
    image = image,
    contentDescription = "Logo of $name", // TODO use i18n
    id = this.id.toJavaUuid(),
)

context(_: BrandRepository)
fun BrandObj.toDomain(image: (BrandObj) -> Any): Brand {
    contract { callsInPlace(image, InvocationKind.EXACTLY_ONCE) }
    return this.toDomain(image(this))
}

context(_: BrandRepository)
fun Brand.toObject() = BrandObj(
    name = this.name,
    description = this.description,
    id = this.id.toKotlinUuid(),
    createdAt = Clock.System.now().toString(),
)
