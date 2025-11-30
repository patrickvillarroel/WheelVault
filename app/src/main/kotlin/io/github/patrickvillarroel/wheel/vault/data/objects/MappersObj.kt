package io.github.patrickvillarroel.wheel.vault.data.objects

import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.TradeProposal
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Instant
import kotlin.uuid.Uuid

// Brand
fun BrandObj.toDomain(image: Any) = Brand(
    name = this.name,
    description = this.description,
    image = image,
    contentDescription = "Logo of $name", // TODO use i18n
    id = this.id!!,
    createdAt = createdAt,
)

inline fun BrandObj.toDomain(image: (BrandObj) -> Any): Brand {
    contract { callsInPlace(image, InvocationKind.EXACTLY_ONCE) }
    return this.toDomain(image(this))
}

// Trades
fun TradeEventTypeObj.toDomain() = when (this) {
    TradeEventTypeObj.PROPOSED -> TradeProposal.TradeEventType.PROPOSED
    TradeEventTypeObj.ACCEPTED -> TradeProposal.TradeEventType.ACCEPTED
    TradeEventTypeObj.REJECTED -> TradeProposal.TradeEventType.REJECTED
    TradeEventTypeObj.CANCELLED -> TradeProposal.TradeEventType.CANCELLED
    TradeEventTypeObj.EXPIRED -> TradeProposal.TradeEventType.EXPIRED
    TradeEventTypeObj.COMPLETED -> TradeProposal.TradeEventType.COMPLETED
}

fun TradeProposalObj.toDomain() = TradeProposal(
    id = this.id?.let { Uuid.parse(it) },
    tradeGroupId = Uuid.parse(this.tradeGroupId),
    requesterId = Uuid.parse(this.requesterId),
    ownerId = Uuid.parse(this.ownerId),
    offeredCarId = Uuid.parse(this.offeredCarId),
    requestedCarId = Uuid.parse(this.requestedCarId),
    eventType = this.eventType.toDomain(),
    message = this.message,
    expiresAt = this.expiresAt?.let { parseInstantWithoutZone(it) },
    createdAt = this.createdAt?.let { parseInstantWithoutZone(it) },
    createdBy = Uuid.parse(this.createdBy),
)

fun CurrentTradeStatusObj.toDomain() = TradeProposal.CurrentTradeStatus(
    tradeGroupId = Uuid.parse(this.tradeGroupId),
    currentStatus = this.currentStatus.toDomain(),
    effectiveStatus = this.effectiveStatus,
    requesterId = Uuid.parse(this.requesterId),
    ownerId = Uuid.parse(this.ownerId),
    offeredCarId = Uuid.parse(this.offeredCarId),
    requestedCarId = Uuid.parse(this.requestedCarId),
    initialMessage = this.initialMessage,
    lastMessage = this.lastMessage,
    proposedAt = parseInstantWithoutZone(this.proposedAt),
    lastUpdated = parseInstantWithoutZone(this.lastUpdated),
    originalExpiresAt = this.originalExpiresAt?.let { parseInstantWithoutZone(it) },
    isActive = this.isActive,
    isSuccessful = this.isSuccessful,
)

/**
 * Parsea un string de timestamp de PostgreSQL que no tiene zona horaria explícita.
 * PostgreSQL devuelve timestamps en formato "2025-12-05T23:14:40.832452"
 * pero Instant.parse() espera "2025-12-05T23:14:40.832452Z"
 */
private fun parseInstantWithoutZone(timestamp: String): Instant {
    // Si ya tiene la Z al final, parsear directamente
    if (timestamp.endsWith("Z")) {
        return Instant.parse(timestamp)
    }
    // Agregar la Z al final para indicar UTC
    return Instant.parse("${timestamp}Z")
}
