package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.confirmation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarDetailCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarErrorScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.ExchangeViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun ExchangeConfirmCarScreen(
    requestCarId: UUID,
    modifier: Modifier = Modifier,
    exchangeViewModel: ExchangeViewModel = koinViewModel(),
) {
    val exchangeConfirmState by exchangeViewModel.exchangeConfirmState.collectAsState()

    LaunchedEffect(requestCarId) {
        exchangeViewModel.offersOf(requestCarId)
    }

    AnimatedContent(exchangeConfirmState) { state ->
        when (state) {
            is ExchangeViewModel.ExchangeConfirmUiState.WaitingConfirm -> ExchangeConfirmCar(
                state.offeredCar,
                state.requestedCar,
                modifier = modifier,
                callbacks = CarDetailCallbacks.default(state.offeredCar),
                onAcceptClick = { exchangeViewModel.acceptExchange(state.offeredCar, state.requestedCar) },
                onCancelClick = { exchangeViewModel.rejectExchange(state.offeredCar, state.requestedCar) },
            )

            ExchangeViewModel.ExchangeConfirmUiState.Accepted -> TODO()
            ExchangeViewModel.ExchangeConfirmUiState.Error -> CarErrorScreen(CarViewModel.CarDetailUiState.Error)
            ExchangeViewModel.ExchangeConfirmUiState.Loading -> Scaffold(
                Modifier.fillMaxSize(),
            ) {
                LoadingIndicator(Modifier.padding(it).fillMaxSize())
            }
            ExchangeViewModel.ExchangeConfirmUiState.Rejected -> TODO()
        }
    }
}
