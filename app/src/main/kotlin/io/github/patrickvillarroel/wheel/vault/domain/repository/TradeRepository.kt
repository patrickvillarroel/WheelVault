package io.github.patrickvillarroel.wheel.vault.domain.repository

import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.model.TradeProposal
import kotlin.uuid.Uuid

interface TradeRepository {
    /**
     * Crear una propuesta de intercambio
     * @param expirationHours Default 7 days
     */
    suspend fun createTradeProposal(
        offeredCarId: Uuid,
        requestedCarId: Uuid,
        message: String? = null,
        expirationHours: Int = 168,
    ): TradeProposal

    suspend fun respondToTrade(tradeGroupId: Uuid, accept: Boolean, responseMessage: String? = null): TradeProposal
    suspend fun cancelTrade(tradeGroupId: Uuid): TradeProposal

    /** Obtener intercambios activos */
    suspend fun getActiveTrades(): List<TradeProposal.CurrentTradeStatus>

    /** Obtener historial completo de un intercambio */
    suspend fun getTradeHistory(tradeGroupId: String): List<TradeProposal>

    suspend fun getAvailableCarsForTrade(): List<CarItem>

    /** Obtener propuestas de intercambio activas donde el carId es el solicitado o el ofrecido */
    suspend fun getActiveTradesForCar(carId: Uuid): List<TradeProposal.CurrentTradeStatus>

    /** Obtener IDs de carros propios que ya están ofrecidos en solicitudes activas */
    suspend fun getMyOfferedCarsInActiveTrades(): Set<Uuid>
}
