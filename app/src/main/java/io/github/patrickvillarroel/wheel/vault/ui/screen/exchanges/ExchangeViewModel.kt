package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _exchangeUiState = MutableStateFlow<ExchangeUiState>(ExchangeUiState.Loading)
    val exchangeState = _exchangeUiState.asStateFlow()

    private val _exchangeConfirmState = MutableStateFlow<ExchangeConfirmUiState>(ExchangeConfirmUiState.Loading)
    val exchangeConfirmState = _exchangeConfirmState.asStateFlow()

    // For managing a new trade proposal being drafted
    private val _selectedOwnCarForOffer = MutableStateFlow<CarItem?>(null)
    // val selectedOwnCarForOffer = _selectedOwnCarForOffer.asStateFlow() // Expose if UI needs to observe it

    // To store the context of an existing trade being viewed/acted upon
    private var currentViewedTradeGroupId: Uuid? = null

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _exchangeUiState.update { ExchangeUiState.Loading }
            try {
                // Populate _exchangeUiState with cars user can see/interact with initially
                // For example, cars available for others to request, or user's own cars.
                // Using getAvailableCarsForTrade as a placeholder for general available cars.
                val availableCars = tradeRepository.getAvailableCarsForTrade()
                _exchangeUiState.update { ExchangeUiState.Success(availableCars) }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Log.e("ExchangeViewModel", "Error loading initial data", e)
                // Handle error
                _exchangeUiState.update { ExchangeUiState.Error }
            }
        }
    }

    /**
     * Call this when the user selects their own car they want to offer in a new trade.
     */
    fun selectOwnCarForOffer(car: CarItem) {
        _selectedOwnCarForOffer.update { car }
    }

    /**
     * Called when a user wants to initiate a new trade for the [requestedCar].
     * Assumes the user's car to offer has been selected via [selectOwnCarForOffer].
     * Sets up the confirmation screen for a *new* trade proposal.
     */
    fun exchangeCar(requestedCar: CarItem) {
        val offeredCar = _selectedOwnCarForOffer.value
        if (offeredCar == null) {
            _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
            return
        }
        currentViewedTradeGroupId = null // Signal that this is for a new proposal
        _exchangeConfirmState.update {
            ExchangeConfirmUiState.WaitingConfirm(
                offeredCar = offeredCar,
                requestedCar = requestedCar,
            )
        }
    }

    /**
     * Called by the UI to finalize and submit a new trade proposal
     * that was previously set up by [exchangeCar].
     */
    fun confirmAndCreateTradeProposal(message: String? = null) {
        viewModelScope.launch {
            val currentState = _exchangeConfirmState.value
            if (currentState is ExchangeConfirmUiState.WaitingConfirm && currentViewedTradeGroupId == null) {
                _exchangeConfirmState.update { ExchangeConfirmUiState.Loading }
                try {
                    tradeRepository.createTradeProposal(
                        offeredCarId = currentState.offeredCar.id,
                        requestedCarId = currentState.requestedCar.id,
                        message = message,
                    )
                    // Decide what state to go to: maybe clear confirm state, show a success message,
                    // or refresh a list of active trades. For now, setting to Accepted as a generic success.
                    _exchangeConfirmState.update { ExchangeConfirmUiState.Accepted }
                } catch (e: Exception) {
                    currentCoroutineContext().ensureActive()
                    Log.e("ExchangeViewModel", "Error creating trade proposal", e)
                    _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
                }
            } else {
                // Not in the correct state to create a proposal or it's for an existing trade
                _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
            }
        }
    }

    /**
     * Fetches details of an existing trade offer related to [carId] and sets up the confirmation screen.
     * If multiple offers exist for the car, it currently picks the first active "proposed" one.
     */
    fun offersOf(carId: Uuid) {
        viewModelScope.launch {
            _exchangeConfirmState.update { ExchangeConfirmUiState.Loading }
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
                Log.e("ExchangeViewModel", "Error fetching trade offers", e)
                _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
            }
        }
    }

    /**
     * Rejects an existing trade proposal currently being viewed.
     * The [offeredCar] and [requestedCar] params are per signature but action uses stored tradeGroupId.
     */
    fun rejectExchange(offeredCar: CarItem, requestedCar: CarItem) {
        viewModelScope.launch {
            val tradeId = currentViewedTradeGroupId
            if (tradeId == null) {
                _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
                return@launch
            }
            _exchangeConfirmState.update { ExchangeConfirmUiState.Loading }
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
                Log.e("ExchangeViewModel", "Error rejecting trade", e)
                _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
            }
        }
    }

    /**
     * Accepts an existing trade proposal currently being viewed.
     * The [offeredCar] and [requestedCar] params are per signature but action uses stored tradeGroupId.
     */
    fun acceptExchange(offeredCar: CarItem, requestedCar: CarItem) {
        viewModelScope.launch {
            val tradeId = currentViewedTradeGroupId
            if (tradeId == null) {
                _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
                return@launch
            }
            _exchangeConfirmState.update { ExchangeConfirmUiState.Loading }
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
                Log.e("ExchangeViewModel", "Error accepting trade", e)
                _exchangeConfirmState.update { ExchangeConfirmUiState.Error }
            }
        }
    }

    sealed interface ExchangeUiState {
        object Loading : ExchangeUiState

        @Immutable
        class Success(@Stable val cars: List<CarItem>) : ExchangeUiState
        object Error : ExchangeUiState
    }

    sealed interface ExchangeConfirmUiState {
        object Loading : ExchangeConfirmUiState

        @Immutable
        class WaitingConfirm(val offeredCar: CarItem, val requestedCar: CarItem) : ExchangeConfirmUiState
        object Accepted : ExchangeConfirmUiState
        object Rejected : ExchangeConfirmUiState
        object Error : ExchangeConfirmUiState
    }
}
