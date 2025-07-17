@file:OptIn(ExperimentalUuidApi::class)

package io.github.patrickvillarroel.wheel.vault.data.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** Brand object of PostgresSQL from Supabase */
@Serializable
data class BrandObj(
    val id: Uuid,
    val name: String,
    val description: String,
    @SerialName("created_at")
    val createdAt: String,
) {
    companion object {
        const val TABLE = "brands"
        const val BUCKET_IMAGES = "brands-images"
    }
}
