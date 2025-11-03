package io.github.patrickvillarroel.wheel.vault.data.objects

import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.TradeProposal
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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
    id = this.id,
    tradeGroupId = this.tradeGroupId,
    requesterId = this.requesterId,
    ownerId = this.ownerId,
    offeredCarId = this.offeredCarId,
    requestedCarId = this.requestedCarId,
    eventType = this.eventType.toDomain(),
    message = this.message,
    expiresAt = this.expiresAt,
    createdAt = this.createdAt,
    createdBy = this.createdBy,
)

fun CurrentTradeStatusObj.toDomain() = TradeProposal.CurrentTradeStatus(
    tradeGroupId = this.tradeGroupId,
    currentStatus = this.currentStatus.toDomain(),
    effectiveStatus = this.effectiveStatus,
    requesterId = this.requesterId,
    ownerId = this.ownerId,
    offeredCarId = this.offeredCarId,
    requestedCarId = this.requestedCarId,
    initialMessage = this.initialMessage,
    lastMessage = this.lastMessage,
    proposedAt = this.proposedAt,
    lastUpdated = this.lastUpdated,
    isActive = this.isActive,
    isSuccessful = this.isSuccessful,
)
