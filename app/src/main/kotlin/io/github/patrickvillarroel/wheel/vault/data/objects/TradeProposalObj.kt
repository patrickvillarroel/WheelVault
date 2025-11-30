package io.github.patrickvillarroel.wheel.vault.data.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
class TradeProposalObj(
    val id: String? = null,
    @SerialName("trade_group_id") val tradeGroupId: String,
    @SerialName("requester_id") val requesterId: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("offered_car_id") val offeredCarId: String,
    @SerialName("requested_car_id") val requestedCarId: String,
    @SerialName("event_type") val eventType: TradeEventTypeObj,
    val message: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("created_by") val createdBy: String,
    val metadata: JsonObject? = null,
)
