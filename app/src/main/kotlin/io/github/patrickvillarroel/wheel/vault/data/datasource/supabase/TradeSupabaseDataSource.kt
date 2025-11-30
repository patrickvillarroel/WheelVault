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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.uuid.Uuid

@Serializable
private data class CarValidationData(
    @SerialName("user_id") val userId: String,
    @SerialName("available_for_trade") val availableForTrade: Boolean,
)

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

        co.touchlab.kermit.Logger.withTag("TradeSupabaseDataSource").d {
            "Creating trade proposal - currentUserId: $currentUserId, offeredCarId: $offeredCarId, requestedCarId: $requestedCarId"
        }

        // Verificar que el auto ofrecido es tuyo y está disponible
        val offeredCar = try {
            supabase.from("cars")
                .select(Columns.list("user_id", "available_for_trade")) {
                    filter {
                        eq("id", offeredCarId)
                    }
                }
                .decodeSingle<CarValidationData>()
        } catch (e: Exception) {
            throw IllegalStateException("El auto que intentas ofrecer no existe o no está disponible", e)
        }

        co.touchlab.kermit.Logger.withTag("TradeSupabaseDataSource").d {
            "Offered car found - user_id: ${offeredCar.userId}, available_for_trade: ${offeredCar.availableForTrade}"
        }

        // Validar que el auto ofrecido es tuyo
        if (offeredCar.userId != currentUserId) {
            throw IllegalStateException("El auto que intentas ofrecer no es tuyo")
        }

        // Validar que el auto ofrecido está disponible para intercambio
        if (!offeredCar.availableForTrade) {
            throw IllegalStateException("Tu auto no está marcado como disponible para intercambio")
        }

        // Obtener información del auto solicitado
        val requestedCar = try {
            supabase.from("cars")
                .select(Columns.list("user_id", "available_for_trade")) {
                    filter {
                        eq("id", requestedCarId)
                    }
                }
                .decodeSingle<CarValidationData>()
        } catch (e: Exception) {
            throw IllegalStateException("El auto solicitado no existe", e)
        }

        co.touchlab.kermit.Logger.withTag("TradeSupabaseDataSource").d {
            "Requested car found - user_id: ${requestedCar.userId}, available_for_trade: ${requestedCar.availableForTrade}"
        }

        val ownerId = requestedCar.userId

        // Validar que el auto solicitado está disponible para intercambio
        if (!requestedCar.availableForTrade) {
            throw IllegalStateException("El auto solicitado no está disponible para intercambio")
        }

        // Validar que no estás intercambiando contigo mismo
        if (ownerId == currentUserId) {
            throw IllegalStateException("No puedes intercambiar con tus propios autos")
        }

        // Calcular fecha de expiración
        val expiresAt = Clock.System.now() + expirationHours.hours

        val dataToInsert = mapOf(
            "requester_id" to currentUserId,
            "owner_id" to ownerId,
            "offered_car_id" to offeredCarId.toString(),
            "requested_car_id" to requestedCarId.toString(),
            "event_type" to "proposed",
            "message" to message,
            "expires_at" to expiresAt.toString(),
            "created_by" to currentUserId,
        )

        co.touchlab.kermit.Logger.withTag("TradeSupabaseDataSource").d {
            "Inserting trade proposal with data: $dataToInsert"
        }

        // Insertar en trade_proposals (event-sourcing)
        val proposal = try {
            supabase.from("trade_proposals")
                .insert(dataToInsert) {
                    select()
                }
                .decodeSingle<TradeProposalObj>()
        } catch (e: Exception) {
            co.touchlab.kermit.Logger.withTag("TradeSupabaseDataSource").e(e) {
                "Failed to insert trade proposal. Error: ${e.message}"
            }
            throw e
        }

        co.touchlab.kermit.Logger.withTag("TradeSupabaseDataSource").d {
            "Trade proposal created successfully: ${proposal.id}"
        }

        return proposal.toDomain()
    }

    override suspend fun respondToTrade(tradeGroupId: Uuid, accept: Boolean, responseMessage: String?): TradeProposal {
        // Obtener el estado actual de la propuesta
        val currentTrade = supabase.from("current_trade_status")
            .select {
                filter {
                    eq("trade_group_id", tradeGroupId.toString())
                }
            }
            .decodeSingle<CurrentTradeStatusObj>()

        // Insertar nuevo evento (accepted o rejected) en trade_proposals
        val eventType = if (accept) "accepted" else "rejected"

        val proposal = supabase.from("trade_proposals")
            .insert(
                mapOf(
                    "trade_group_id" to tradeGroupId.toString(),
                    "requester_id" to currentTrade.requesterId,
                    "owner_id" to currentTrade.ownerId,
                    "offered_car_id" to currentTrade.offeredCarId,
                    "requested_car_id" to currentTrade.requestedCarId,
                    "event_type" to eventType,
                    "message" to responseMessage,
                ),
            ) {
                select()
            }.decodeSingle<TradeProposalObj>()

        return proposal.toDomain()
    }

    override suspend fun cancelTrade(tradeGroupId: Uuid): TradeProposal {
        // Obtener el estado actual de la propuesta
        val currentTrade = supabase.from("current_trade_status")
            .select {
                filter {
                    eq("trade_group_id", tradeGroupId.toString())
                }
            }
            .decodeSingle<CurrentTradeStatusObj>()

        // Insertar nuevo evento de cancelación en trade_proposals
        val proposal = supabase.from("trade_proposals")
            .insert(
                mapOf(
                    "trade_group_id" to tradeGroupId.toString(),
                    "requester_id" to currentTrade.requesterId,
                    "owner_id" to currentTrade.ownerId,
                    "offered_car_id" to currentTrade.offeredCarId,
                    "requested_car_id" to currentTrade.requestedCarId,
                    "event_type" to "cancelled",
                    "message" to "Propuesta cancelada",
                ),
            ) {
                select()
            }.decodeSingle<TradeProposalObj>()

        return proposal.toDomain()
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
        // Obtener todos los eventos de este trade_group_id
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

    override suspend fun getMyOfferedCarsInActiveTrades(): Set<Uuid> {
        val currentUserId = supabase.auth.currentUserOrNull()?.id
            ?: return emptySet()

        val myActiveTrades = supabase.from("current_trade_status")
            .select {
                filter {
                    eq("is_active", true)
                    eq("requester_id", currentUserId)
                }
            }.decodeList<CurrentTradeStatusObj>()

        return myActiveTrades.map { Uuid.parse(it.offeredCarId) }.toSet()
    }
}
