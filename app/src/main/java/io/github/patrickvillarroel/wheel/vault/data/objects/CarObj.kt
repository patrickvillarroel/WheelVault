@file:OptIn(ExperimentalUuidApi::class)

package io.github.patrickvillarroel.wheel.vault.data.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** A car object of PostgresSQL from Supabase */
@Serializable
data class CarObj(
    val id: Uuid? = null,
    val model: String,
    val year: Int,
    val brand: String,
    val manufacturer: String,
    val category: String?,
    val description: String?,
    val quantity: Int,
    val isFavorite: Boolean,
    @SerialName("created_at")
    val createdAt: String? = null,
) {
    companion object {
        const val TABLE = "cars"
        const val BUCKET_IMAGES = "cars-images"
    }
}
