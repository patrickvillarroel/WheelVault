package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.confirmation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarErrorScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.ExchangeViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid

@Composable
fun ExchangeConfirmCarScreen(
    requestCarId: Uuid,
    headerBackCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
    isRespondingToOffer: Boolean = false,
    exchangeViewModel: ExchangeViewModel = org.koin.compose.koinInject(),
) {
    val exchangeConfirmState by exchangeViewModel.exchangeConfirmState.collectAsState()

    LaunchedEffect(requestCarId, isRespondingToOffer) {
        co.touchlab.kermit.Logger.withTag("ExchangeConfirmCarScreen").d {
            "LaunchedEffect - requestCarId: $requestCarId, isRespondingToOffer: $isRespondingToOffer, currentState: $exchangeConfirmState"
        }
        // Solo cargar ofertas si estamos respondiendo a una existente
        if (isRespondingToOffer) {
            co.touchlab.kermit.Logger.withTag("ExchangeConfirmCarScreen").d { "Calling offersOf($requestCarId)" }
            exchangeViewModel.offersOf(requestCarId)
        } else {
            co.touchlab.kermit.Logger.withTag("ExchangeConfirmCarScreen").d {
                "Not responding to offer - state should already be configured. Current state: $exchangeConfirmState"
            }
        }
        // Si no, el estado ya fue configurado por exchangeCar()
        // PERO si por alguna razón no está configurado, mostramos error
        if (!isRespondingToOffer && exchangeConfirmState is ExchangeViewModel.ExchangeConfirmUiState.Loading) {
            co.touchlab.kermit.Logger.withTag("ExchangeConfirmCarScreen").w {
                "State is still Loading for new proposal - this may indicate an issue"
            }
        }
    }

    AnimatedContent(exchangeConfirmState) { state ->
        when (state) {
            is ExchangeViewModel.ExchangeConfirmUiState.WaitingConfirm -> {
                co.touchlab.kermit.Logger.withTag("ExchangeConfirmCarScreen").d {
                    "Showing WaitingConfirm - offeredCar: ${state.offeredCar.id}, requestedCar: ${state.requestedCar.id}, message: ${state.message}"
                }
                ExchangeConfirmCar(
                    state.offeredCar,
                    state.requestedCar,
                    modifier = modifier,
                    callbacks = headerBackCallbacks,
                    message = state.message,
                    isRespondingToOffer = isRespondingToOffer,
                    onAcceptClick = {
                        if (isRespondingToOffer) {
                            co.touchlab.kermit.Logger.withTag("ExchangeConfirmCarScreen").d { "onAcceptClick - Accepting existing offer" }
                            // Respondiendo a una oferta existente
                            exchangeViewModel.acceptExchange(state.offeredCar, state.requestedCar)
                        } else {
                            co.touchlab.kermit.Logger.withTag("ExchangeConfirmCarScreen").d {
                                "onAcceptClick - Creating new proposal with message: ${state.message}"
                            }
                            // Creando una nueva propuesta
                            exchangeViewModel.confirmAndCreateTradeProposal(state.message)
                        }
                    },
                    onCancelClick = {
                        if (isRespondingToOffer) {
                            co.touchlab.kermit.Logger.withTag("ExchangeConfirmCarScreen").d { "onCancelClick - Rejecting existing offer" }
                            // Rechazando una oferta existente
                            exchangeViewModel.rejectExchange(state.offeredCar, state.requestedCar)
                        } else {
                            co.touchlab.kermit.Logger.withTag("ExchangeConfirmCarScreen").d { "onCancelClick - Canceling new proposal" }
                            // Cancelando la creación de una propuesta nueva
                            headerBackCallbacks.onBackClick()
                        }
                    },
                )
            }

            ExchangeViewModel.ExchangeConfirmUiState.Accepted -> ExchangeResultScreen(
                isAccepted = true,
                onNavigateBack = headerBackCallbacks.onBackClick,
                modifier = modifier,
            )
            ExchangeViewModel.ExchangeConfirmUiState.Error -> CarErrorScreen(CarViewModel.CarDetailUiState.Error)
            ExchangeViewModel.ExchangeConfirmUiState.Loading -> Scaffold(
                Modifier.fillMaxSize(),
            ) {
                LoadingIndicator(Modifier.padding(it).fillMaxSize())
            }
            ExchangeViewModel.ExchangeConfirmUiState.Rejected -> ExchangeResultScreen(
                isAccepted = false,
                onNavigateBack = headerBackCallbacks.onBackClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ExchangeResultScreen(isAccepted: Boolean, onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(32.dp),
            ) {
                Icon(
                    imageVector = if (isAccepted) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                    contentDescription =
                    if (isAccepted) stringResource(R.string.completed) else stringResource(R.string.cancel),
                    tint = if (isAccepted) Color(0xFF4CAF50) else Color(0xFFE42E31),
                    modifier = Modifier.size(120.dp),
                )

                Text(
                    text = if (isAccepted) {
                        stringResource(R.string.exchange_accepted_title)
                    } else {
                        stringResource(R.string.exchange_rejected_title)
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = if (isAccepted) {
                        stringResource(R.string.exchange_accepted_message)
                    } else {
                        stringResource(R.string.exchange_rejected_message)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Text(text = stringResource(R.string.back))
                }
            }
        }
    }
}
