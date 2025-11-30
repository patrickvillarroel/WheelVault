package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.model.TradeProposal
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.TradeRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class ExchangeViewModel(private val tradeRepository: TradeRepository, private val carsRepository: CarsRepository) :
    ViewModel() {
    companion object {
        private val logger = Logger.withTag("ExchangeViewModel")
    }

    private val _exchangeState = MutableStateFlow<ExchangeUiState>(ExchangeUiState.Loading)
    val exchangeState = _exchangeState.asStateFlow()

    private val _exchangeConfirmState = MutableStateFlow<ExchangeConfirmUiState>(ExchangeConfirmUiState.Loading)
    val exchangeConfirmState = _exchangeConfirmState.asStateFlow()

    // For managing a new trade proposal being drafted
    private val selectedOwnCarForOffer = MutableStateFlow<CarItem?>(null)
    // val selectedOwnCarForOffer = _selectedOwnCarForOffer.asStateFlow() // Expose if UI needs to observe it

    // To store the context of an existing trade being viewed/acted upon
    private var currentViewedTradeGroupId: Uuid? = null

    // Pagination state
    private var currentPage = 0
    private val pageSize = 20
    private var isLoadingMore = false
    private var hasMorePages = true

    fun loadInitialData(forceRefresh: Boolean = false) {
        // No recargar si ya tenemos datos exitosos y no es refresh forzado
        if (!forceRefresh && _exchangeState.value is ExchangeUiState.Success) {
            logger.d { "Skipping reload - data already loaded" }
            return
        }

        currentPage = 0
        hasMorePages = false // Deshabilitado por ahora hasta implementar paginación en backend
        _exchangeState.update { ExchangeUiState.Loading }
        viewModelScope.launch {
            try {
                val availableCars = tradeRepository.getAvailableCarsForTrade()
                _exchangeState.update {
                    ExchangeUiState.Success(
                        cars = availableCars,
                        isLoadingMore = false,
                        hasMore = false, // Sin paginación por ahora
                    )
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Error loading initial data", e)
                _exchangeState.update { ExchangeUiState.Error }
            }
        }
    }

    fun loadMoreCars() {
        // TODO: Implementar cuando CarSupabaseDataSource soporte paginación
        // Por ahora no hace nada
    }

    /**
     * Call this when the user selects their own car they want to offer in a new trade.
     * This updates both the internal state and the confirmation UI state if a requested car exists.
     */
    fun selectOwnCarForOffer(car: CarItem) {
        logger.d { "selectOwnCarForOffer - car: ${car.id}, ${car.brand}, ${car.model}" }
        selectedOwnCarForOffer.update { car }

        // Si ya hay un requestedCar en el estado, actualizar con el offeredCar
        val currentState = _exchangeConfirmState.value
        if (currentState is ExchangeConfirmUiState.WaitingConfirm) {
            logger.d { "Updating WaitingConfirm state with offeredCar: ${car.id}" }
            _exchangeConfirmState.update {
                ExchangeConfirmUiState.WaitingConfirm(
                    offeredCar = car,
                    requestedCar = currentState.requestedCar,
                    message = currentState.message,
                )
            }
        }
    }

    /**
     * Called when a user wants to initiate a new trade for the [requestedCar].
     * The user's car to offer will be selected later via [selectOwnCarForOffer].
     * Sets up the confirmation screen for a *new* trade proposal.
     */
    fun exchangeCar(requestedCar: CarItem) {
        logger.d { "exchangeCar - requestedCar: ${requestedCar.id}, ${requestedCar.brand}, ${requestedCar.model}" }
        val offeredCar = selectedOwnCarForOffer.value

        // Si no hay offeredCar todavía, usar un placeholder temporal
        // Se actualizará cuando el usuario seleccione su auto en ExchangeCarSelectionScreen
        val carToUse = offeredCar ?: requestedCar // Temporal placeholder

        currentViewedTradeGroupId = null // Signal that this is for a new proposal
        _exchangeConfirmState.update {
            ExchangeConfirmUiState.WaitingConfirm(
                offeredCar = carToUse,
                requestedCar = requestedCar,
            )
        }
        logger.d {
            "exchangeCar - Set WaitingConfirm state. offeredCar=${carToUse.id}, requestedCar=${requestedCar.id}"
        }
    }

    /**
     * Called by the UI to finalize and submit a new trade proposal
     * that was previously set up by [exchangeCar].
     */
    fun confirmAndCreateTradeProposal(message: String? = null) {
        val currentState = _exchangeConfirmState.value
        logger.d {
            "confirmAndCreateTradeProposal - currentState: $currentState, tradeGroupId: $currentViewedTradeGroupId"
        }

        if (currentState is ExchangeConfirmUiState.WaitingConfirm && currentViewedTradeGroupId == null) {
            _exchangeConfirmState.update { ExchangeConfirmUiState.Loading }
            viewModelScope.launch {
                try {
                    logger.d {
                        "Creating trade proposal: offered=${currentState.offeredCar.id}, requested=${currentState.requestedCar.id}"
                    }

                    // Usar withTimeout para evitar colgarse indefinidamente
                    val result = kotlinx.coroutines.withTimeout(30000L) {
                        // 30 segundos timeout
                        tradeRepository.createTradeProposal(
                            offeredCarId = currentState.offeredCar.id,
                            requestedCarId = currentState.requestedCar.id,
                            message = message,
                        )
                    }

                    logger.d { "Trade proposal created successfully: ${result.id}" }
                    _exchangeConfirmState.update { ExchangeConfirmUiState.Accepted }
                } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                    logger.e(e) { "Timeout creating trade proposal - took longer than 30 seconds" }
                    _exchangeConfirmState.update {
                        ExchangeConfirmUiState.ErrorWithMessage("La operación tardó demasiado. Intenta nuevamente.")
                    }
                } catch (e: IllegalStateException) {
                    currentCoroutineContext().ensureActive()
                    logger.e(e) { "IllegalState: ${e.message}" }
                    _exchangeConfirmState.update {
                        ExchangeConfirmUiState.ErrorWithMessage(e.message ?: "Error al crear la propuesta")
                    }
                } catch (e: Exception) {
                    currentCoroutineContext().ensureActive()
                    logger.e(e) { "Error creating trade proposal: ${e.message}" }
                    logger.e(e) { "Full exception: $e" }
                    logger.e(e) { "Exception class: ${e::class.simpleName}" }

                    // Extraer mensaje de error específico
                    val errorMessage = when {
                        e.message?.contains("Ya existe una propuesta activa para estos autos") == true ->
                            "Ya tienes una propuesta activa con estos mismos autos."
                        e.message?.contains("Tu auto ya está en una propuesta activa") == true ->
                            "Tu auto ya está en una propuesta activa. Espera a que se resuelva antes de crear otra."
                        e.message?.contains("no está disponible para intercambio") == true ->
                            e.message ?: "El auto solicitado no está disponible para intercambio."
                        e.message?.contains("no existe o fue eliminado") == true ->
                            e.message ?: "El auto solicitado no existe o fue eliminado."
                        e.message?.contains("El auto solicitado ya está en una propuesta activa") == true ->
                            "El auto que deseas ya está en una propuesta activa de otro usuario."
                        e.message?.contains("not found") == true ->
                            "El auto solicitado no está disponible para intercambio."
                        e.message?.contains("Could not find owner") == true ->
                            "No se pudo encontrar al propietario del auto solicitado."
                        else -> "Error: ${e.message ?: "Error desconocido al crear la propuesta"}"
                    }

                    _exchangeConfirmState.update { ExchangeConfirmUiState.ErrorWithMessage(errorMessage) }
                }
            }
        } else {
            logger.e {
                "Cannot create proposal - invalid state or existing trade. State: $currentState, TradeId: $currentViewedTradeGroupId"
            }
            _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
        }
    }

    /**
     * Fetches details of an existing trade offer related to [carId] and sets up the confirmation screen.
     * If multiple offers exist for the car, it currently picks the first active "proposed" one.
     */
    fun offersOf(carId: Uuid) {
        _exchangeConfirmState.update { ExchangeConfirmUiState.Loading }
        viewModelScope.launch {
            try {
                val tradesForCar = tradeRepository.getActiveTradesForCar(carId)
                // Find a suitable trade to display - e.g., first active one that is 'PROPOSED'
                val relevantTrade = tradesForCar.firstOrNull {
                    it.currentStatus == TradeProposal.TradeEventType.PROPOSED && it.isActive
                }

                if (relevantTrade != null) {
                    currentViewedTradeGroupId = relevantTrade.tradeGroupId
                    val offeredCarDetails = carsRepository.fetch(relevantTrade.offeredCarId)
                    val requestedCarDetails = carsRepository.fetch(relevantTrade.requestedCarId)

                    if (offeredCarDetails != null && requestedCarDetails != null) {
                        _exchangeConfirmState.update {
                            ExchangeConfirmUiState.WaitingConfirm(
                                offeredCar = offeredCarDetails,
                                requestedCar = requestedCarDetails,
                                message = relevantTrade.initialMessage,
                            )
                        }
                    } else {
                        _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
                    }
                } else {
                    // No suitable active proposal found for this car to confirm/reject.
                    _exchangeConfirmState.update { ExchangeConfirmUiState.Error } // Or a state indicating no offers
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Error fetching trade offers", e)
                _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
            }
        }
    }

    /**
     * Rejects an existing trade proposal currently being viewed.
     * The [offeredCar] and [requestedCar] params are per signature but action uses stored tradeGroupId.
     */
    fun rejectExchange(offeredCar: CarItem, requestedCar: CarItem) {
        val tradeId = currentViewedTradeGroupId
        if (tradeId == null) {
            _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
            return
        }
        _exchangeConfirmState.update { ExchangeConfirmUiState.Loading }
        viewModelScope.launch {
            try {
                tradeRepository.respondToTrade(
                    tradeGroupId = tradeId,
                    accept = false,
                    responseMessage = "Rejected by user",
                )
                _exchangeConfirmState.update { ExchangeConfirmUiState.Rejected }
                currentViewedTradeGroupId = null
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Error rejecting trade", e)
                _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
            }
        }
    }

    /**
     * Accepts an existing trade proposal currently being viewed.
     * The [offeredCar] and [requestedCar] params are per signature but action uses stored tradeGroupId.
     */
    fun acceptExchange(offeredCar: CarItem, requestedCar: CarItem) {
        val tradeId = currentViewedTradeGroupId
        if (tradeId == null) {
            _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
            return
        }
        _exchangeConfirmState.update { ExchangeConfirmUiState.Loading }
        viewModelScope.launch {
            try {
                tradeRepository.respondToTrade(
                    tradeGroupId = tradeId,
                    accept = true,
                    responseMessage = "Accepted by user",
                )
                _exchangeConfirmState.update { ExchangeConfirmUiState.Accepted }
                currentViewedTradeGroupId = null
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                logger.e("Error accepting trade", e)
                _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
            }
        }
    }

    /**
     * Resets the exchange confirmation state to Loading.
     * Call this when navigating away from the confirmation screen.
     */
    fun resetExchangeConfirmState() {
        logger.d { "resetExchangeConfirmState - Resetting to Loading" }
        _exchangeConfirmState.update { ExchangeConfirmUiState.Loading }
        currentViewedTradeGroupId = null
        selectedOwnCarForOffer.update { null }
    }

    sealed interface ExchangeUiState {
        data object Loading : ExchangeUiState

        @Immutable
        data class Success(
            @Stable val cars: List<CarItem>,
            val isLoadingMore: Boolean = false,
            val hasMore: Boolean = true,
        ) : ExchangeUiState
        data object Error : ExchangeUiState
    }

    sealed interface ExchangeConfirmUiState {
        data object Loading : ExchangeConfirmUiState

        @Immutable
        data class WaitingConfirm(val offeredCar: CarItem, val requestedCar: CarItem, val message: String? = null) :
            ExchangeConfirmUiState
        data object Accepted : ExchangeConfirmUiState
        data object Rejected : ExchangeConfirmUiState
        data object Error : ExchangeConfirmUiState
        data class ErrorWithMessage(val errorMessage: String) : ExchangeConfirmUiState
    }
}
