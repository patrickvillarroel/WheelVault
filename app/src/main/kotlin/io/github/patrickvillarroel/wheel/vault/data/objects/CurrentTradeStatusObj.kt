package io.github.patrickvillarroel.wheel.vault.data.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
class CurrentTradeStatusObj(
    @SerialName("trade_group_id") val tradeGroupId: Uuid,
    @SerialName("current_status") val currentStatus: TradeEventTypeObj,
    @SerialName("effective_status") val effectiveStatus: String,
    @SerialName("requester_id") val requesterId: Uuid,
    @SerialName("owner_id") val ownerId: Uuid,
    @SerialName("offered_car_id") val offeredCarId: Uuid,
    @SerialName("requested_car_id") val requestedCarId: Uuid,
    @SerialName("initial_message") val initialMessage: String? = null,
    @SerialName("last_message") val lastMessage: String? = null,
    @SerialName("proposed_at") val proposedAt: Instant,
    @SerialName("last_updated") val lastUpdated: Instant,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("is_successful") val isSuccessful: Boolean,
)
