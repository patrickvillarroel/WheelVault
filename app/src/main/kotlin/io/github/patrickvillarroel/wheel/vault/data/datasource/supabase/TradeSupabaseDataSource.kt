package io.github.patrickvillarroel.wheel.vault.data.datasource.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.patrickvillarroel.wheel.vault.data.objects.CurrentTradeStatusObj
import io.github.patrickvillarroel.wheel.vault.data.objects.TradeProposalObj
import io.github.patrickvillarroel.wheel.vault.data.objects.toDomain
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.model.TradeProposal
import io.github.patrickvillarroel.wheel.vault.domain.repository.TradeRepository
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.uuid.Uuid

class TradeSupabaseDataSource(
    private val supabase: SupabaseClient,
    private val carSupabaseDataSource: CarSupabaseDataSource,
) : TradeRepository {
    override suspend fun createTradeProposal(
        offeredCarId: Uuid,
        requestedCarId: Uuid,
        message: String?,
        expirationHours: Int,
    ): TradeProposal {
        val currentUserId = supabase.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("User not authenticated")

        // Obtener información del auto solicitado
        val requestedCar = supabase.from("cars")
            .select(Columns.list("user_id")) {
                filter {
                    eq("id", requestedCarId)
                    eq("available_for_trade", true)
                }
            }
            .decodeSingle<Map<String, String>>()

        val ownerId = requestedCar["user_id"]
            ?: throw IllegalStateException("Could not find owner of requested car")

        val expiresAt = Clock.System.now() + expirationHours.hours

        val proposal = supabase.from("trade_proposals")
            .insert(
                mapOf(
                    "requester_id" to currentUserId,
                    "owner_id" to ownerId,
                    "offered_car_id" to offeredCarId,
                    "requested_car_id" to requestedCarId,
                    "event_type" to "proposed",
                    "message" to message,
                    "expires_at" to expiresAt,
                ),
            ) {
                select()
            }.decodeSingle<TradeProposalObj>()

        return proposal.toDomain()
    }

    override suspend fun respondToTrade(tradeGroupId: Uuid, accept: Boolean, responseMessage: String?): TradeProposal {
        // Obtener información del trade para validaciones
        val tradeInfo = supabase.from("current_trade_status")
            .select {
                filter {
                    eq("trade_group_id", tradeGroupId)
                    eq("effective_status", "proposed")
                }
            }.decodeSingle<CurrentTradeStatusObj>()

        val eventType = if (accept) "accepted" else "rejected"

        val response = supabase.from("trade_proposals")
            .insert(
                mapOf(
                    "trade_group_id" to tradeGroupId,
                    "requester_id" to tradeInfo.requesterId,
                    "owner_id" to tradeInfo.ownerId,
                    "offered_car_id" to tradeInfo.offeredCarId,
                    "requested_car_id" to tradeInfo.requestedCarId,
                    "event_type" to eventType,
                    "message" to responseMessage,
                ),
            ) {
                select()
            }.decodeSingle<TradeProposalObj>()

        return response.toDomain()
    }

    override suspend fun cancelTrade(tradeGroupId: Uuid): TradeProposal {
        val tradeInfo = supabase.from("current_trade_status")
            .select {
                filter {
                    eq("trade_group_id", tradeGroupId)
                    eq("effective_status", "proposed")
                }
            }.decodeSingle<CurrentTradeStatusObj>()

        val cancellation = supabase.from("trade_proposals")
            .insert(
                mapOf(
                    "trade_group_id" to tradeGroupId,
                    "requester_id" to tradeInfo.requesterId,
                    "owner_id" to tradeInfo.ownerId,
                    "offered_car_id" to tradeInfo.offeredCarId,
                    "requested_car_id" to tradeInfo.requestedCarId,
                    "event_type" to "cancelled",
                    "message" to "Propuesta cancelada por el solicitante",
                ),
            ) {
                select()
            }.decodeSingle<TradeProposalObj>()

        return cancellation.toDomain()
    }

    override suspend fun getActiveTrades(): List<TradeProposal.CurrentTradeStatus> {
        val currentUserId = supabase.auth.currentUserOrNull()!!.id

        val trades = supabase.from("current_trade_status")
            .select {
                filter {
                    eq("is_active", true)
                    or {
                        eq("requester_id", currentUserId)
                        eq("owner_id", currentUserId)
                    }
                }
                order("proposed_at", Order.DESCENDING)
            }.decodeList<CurrentTradeStatusObj>()

        return trades.map { it.toDomain() }
    }

    override suspend fun getTradeHistory(tradeGroupId: String): List<TradeProposal> {
        val history = supabase.from("trade_proposals")
            .select {
                filter {
                    eq("trade_group_id", tradeGroupId)
                }
                order("created_at", Order.ASCENDING)
            }.decodeList<TradeProposalObj>()

        return history.map { it.toDomain() }
    }

    override suspend fun getAvailableCarsForTrade(): List<CarItem> =
        carSupabaseDataSource.getCarsForTrade()

    override suspend fun getActiveTradesForCar(carId: Uuid): List<TradeProposal.CurrentTradeStatus> {
        val trades = supabase.from("current_trade_status")
            .select {
                filter {
                    eq("is_active", true)
                    or {
                        eq("offered_car_id", carId)
                        eq("requested_car_id", carId)
                    }
                }
                order("proposed_at", Order.DESCENDING)
            }.decodeList<CurrentTradeStatusObj>()
        return trades.map { it.toDomain() }
    }
}
