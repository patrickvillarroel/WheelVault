package io.github.patrickvillarroel.wheel.vault.domain.model

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class TradeProposal(
    val id: Uuid? = null,
    val tradeGroupId: Uuid,
    val requesterId: Uuid,
    val ownerId: Uuid,
    val offeredCarId: Uuid,
    val requestedCarId: Uuid,
    val eventType: TradeEventType,
    val message: String? = null,
    val expiresAt: Instant? = null,
    val createdAt: Instant? = null,
    val createdBy: Uuid,
) {
    enum class TradeEventType {
        /** Propuesta inicial*/
        PROPOSED,

        /**Propuesta aceptada*/
        ACCEPTED,

        /** Propuesta rechazada */
        REJECTED,

        /** Cancelada por solicitante */
        CANCELLED,

        /** Expirada automáticamente */
        EXPIRED,

        /** Intercambio físico completado */
        COMPLETED,
    }

    data class CurrentTradeStatus(
        val tradeGroupId: Uuid,
        val currentStatus: TradeEventType,
        val effectiveStatus: String,
        val requesterId: Uuid,
        val ownerId: Uuid,
        val offeredCarId: Uuid,
        val requestedCarId: Uuid,
        val initialMessage: String? = null,
        val lastMessage: String? = null,
        val proposedAt: Instant,
        val lastUpdated: Instant,
        val isActive: Boolean,
        val isSuccessful: Boolean,
    )
}
