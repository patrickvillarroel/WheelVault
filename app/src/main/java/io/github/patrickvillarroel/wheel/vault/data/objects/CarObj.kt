@file:OptIn(ExperimentalUuidApi::class)

package io.github.patrickvillarroel.wheel.vault.data.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** A car object of PostgresSQL from Supabase */
@Serializable
data class CarObj(
    val model: String,
    val year: Int,
    val brand: String,
    val manufacturer: String,
    val category: String?,
    val description: String?,
    val quantity: Int,
    val isFavorite: Boolean,
    val id: Uuid? = null,
    @SerialName("available_for_trade")
    val availableForTrade: Boolean = false,
) {
    companion object {
        const val TABLE = "cars"
        const val BUCKET_IMAGES = "cars-images"
        const val USER_ID_FIELD = "user_id"
        const val FULL_TEXT_SEARCH_FIELD = "document_with_weights"
    }
}
