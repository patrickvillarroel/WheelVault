package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.offer

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.ExchangeViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid

private val logger = Logger.withTag("ExchangeCarOfferScreen")

@Composable
fun ExchangeCarOfferScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onExchangeTemporalClick: (Uuid) -> Unit,
    headerCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
    exchangeViewModel: ExchangeViewModel = koinViewModel(),
) {
    // Obtener el estado del ViewModel
    val exchangeConfirmState by exchangeViewModel.exchangeConfirmState.collectAsStateWithLifecycle()

    // ExchangeCarOfferScreen muestra el auto que el usuario ACABA DE SELECCIONAR (offeredCar)
    // para confirmar "¿Quieres ofrecer ESTE auto en intercambio?"
    when (val state = exchangeConfirmState) {
        is ExchangeViewModel.ExchangeConfirmUiState.WaitingConfirm -> {
            logger.d {
                "Showing offered car (the one user selected): ${state.offeredCar.id}, ${state.offeredCar.brand}, ${state.offeredCar.model}"
            }
            ExchangeCarOffer(
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                carDetail = state.offeredCar, // Mostrar el auto que el usuario OFRECE (el que acaba de seleccionar)
                callbacks = headerCallbacks,
                onExchangeClick = {
                    logger.d {
                        "onExchangeClick - Navigating to confirmation. Offered: ${state.offeredCar.id}, Requested: ${state.requestedCar.id}"
                    }
                    onExchangeTemporalClick(state.requestedCar.id)
                },
                modifier = modifier,
            )
        }

        else -> {
            // Si el estado no es WaitingConfirm, mostrar loading o error
            Scaffold(Modifier.fillMaxSize()) { paddingValues ->
                LoadingIndicator(Modifier.padding(paddingValues).fillMaxSize())
            }
        }
    }
}
