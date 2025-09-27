package io.github.patrickvillarroel.wheel.vault.data.objects

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
class VideoObj(val id: Uuid, val name: String, val description: String?, val link: String) {
    companion object {
        const val TABLE = "news"
        const val BUCKET = "news-thumbnails"
    }
}
