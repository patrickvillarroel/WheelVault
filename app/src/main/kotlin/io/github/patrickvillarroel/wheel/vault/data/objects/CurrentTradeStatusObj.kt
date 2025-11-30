package io.github.patrickvillarroel.wheel.vault.data.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CurrentTradeStatusObj(
    @SerialName("trade_group_id") val tradeGroupId: String,
    @SerialName("current_status") val currentStatus: TradeEventTypeObj,
    @SerialName("effective_status") val effectiveStatus: String,
    @SerialName("requester_id") val requesterId: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("offered_car_id") val offeredCarId: String,
    @SerialName("requested_car_id") val requestedCarId: String,
    @SerialName("initial_message") val initialMessage: String? = null,
    @SerialName("last_message") val lastMessage: String? = null,
    @SerialName("proposed_at") val proposedAt: String,
    @SerialName("last_updated") val lastUpdated: String,
    @SerialName("original_expires_at") val originalExpiresAt: String? = null,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("is_successful") val isSuccessful: Boolean,
)
