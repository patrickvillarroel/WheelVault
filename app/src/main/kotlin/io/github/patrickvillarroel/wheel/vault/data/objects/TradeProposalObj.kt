package io.github.patrickvillarroel.wheel.vault.data.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
class TradeProposalObj(
    val id: Uuid? = null,
    @SerialName("trade_group_id") val tradeGroupId: Uuid,
    @SerialName("requester_id") val requesterId: Uuid,
    @SerialName("owner_id") val ownerId: Uuid,
    @SerialName("offered_car_id") val offeredCarId: Uuid,
    @SerialName("requested_car_id") val requestedCarId: Uuid,
    @SerialName("event_type") val eventType: TradeEventTypeObj,
    val message: String? = null,
    @SerialName("expires_at") val expiresAt: Instant? = null,
    @SerialName("created_at") val createdAt: Instant? = null,
    @SerialName("created_by") val createdBy: Uuid,
    val metadata: JsonObject? = null,
)
