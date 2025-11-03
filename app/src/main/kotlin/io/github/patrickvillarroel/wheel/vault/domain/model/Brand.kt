package io.github.patrickvillarroel.wheel.vault.domain.model

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class Brand(
    val id: Uuid,
    val name: String,
    val description: String,
    val image: Any,
    val contentDescription: String?,
    val createdAt: Instant?,
) {
    companion object {
        @JvmField
        val DEFAULT_IMAGE = io.github.patrickvillarroel.wheel.vault.R.drawable.no_picture_available
    }
}
