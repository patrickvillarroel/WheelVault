package io.github.patrickvillarroel.wheel.vault.domain.model

import java.util.UUID

data class VideoNews(
    val id: UUID,
    val name: String,
    val link: String,
    val thumbnail: Any,
    val description: String?)
{
    companion object {
         @JvmField
         val DEFAULT_IMAGE = io.github.patrickvillarroel.wheel.vault.R.drawable.no_picture_available
    }
}

