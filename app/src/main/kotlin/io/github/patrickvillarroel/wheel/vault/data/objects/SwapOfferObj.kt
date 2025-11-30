package io.github.patrickvillarroel.wheel.vault.data.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SwapOfferObj(
    val id: String,
    @SerialName("requester_car_id") val requesterCarId: String,
    @SerialName("target_user_id") val targetUserId: String,
    @SerialName("target_car_id") val targetCarId: String,
    val status: String, // pending, accepted, rejected, cancelled
    val message: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String? = null,
)
