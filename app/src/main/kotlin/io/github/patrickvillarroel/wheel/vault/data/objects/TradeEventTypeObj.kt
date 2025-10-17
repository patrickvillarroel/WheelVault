package io.github.patrickvillarroel.wheel.vault.data.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TradeEventTypeObj {
    /** Propuesta inicial*/
    @SerialName("proposed")
    PROPOSED,

    /**Propuesta aceptada*/
    @SerialName("accepted")
    ACCEPTED,

    /** Propuesta rechazada */
    @SerialName("rejected")
    REJECTED,

    /** Cancelada por solicitante */
    @SerialName("cancelled")
    CANCELLED,

    /** Expirada automáticamente */
    @SerialName("expired")
    EXPIRED,

    /** Intercambio físico completado */
    @SerialName("completed")
    COMPLETED,
}
