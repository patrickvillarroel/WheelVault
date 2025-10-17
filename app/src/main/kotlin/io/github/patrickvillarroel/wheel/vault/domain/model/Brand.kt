package io.github.patrickvillarroel.wheel.vault.domain.model

import kotlin.uuid.Uuid

data class Brand(
    val name: String,
    val description: String,
    val image: Any,
    val contentDescription: String?,
    val id: Uuid = Uuid.random(),
) {
    companion object {
        @JvmField
        val DEFAULT_IMAGE = io.github.patrickvillarroel.wheel.vault.R.drawable.no_picture_available
    }
}
