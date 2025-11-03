package io.github.patrickvillarroel.wheel.vault.domain.model

import kotlin.uuid.Uuid

data class VideoNews(val id: Uuid, val name: String, val link: String, val thumbnail: Any, val description: String?) {
    companion object {
        @JvmField
        val DEFAULT_IMAGE = io.github.patrickvillarroel.wheel.vault.R.drawable.no_picture_available
    }
}
