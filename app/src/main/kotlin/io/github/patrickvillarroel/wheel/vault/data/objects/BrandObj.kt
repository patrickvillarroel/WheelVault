@file:OptIn(ExperimentalUuidApi::class)

package io.github.patrickvillarroel.wheel.vault.data.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** Brand object of PostgresSQL from Supabase */
@Serializable
class BrandObj(
    val id: Uuid? = null,
    val name: String,
    val description: String,
    @SerialName("created_at")
    val createdAt: Instant? = null,
) {
    companion object {
        const val TABLE = "brands"
        const val BUCKET_IMAGES = "brands-images"
    }
}
