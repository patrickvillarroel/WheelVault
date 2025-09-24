package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.offer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarDetailCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarErrorScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.ExchangeViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun ExchangeCarOfferScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    carId: UUID,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel = koinViewModel(),
    exchangeViewModel: ExchangeViewModel = koinViewModel(),
) {
    // TODO connect new uiState when exchangeCar is confirmed, need to add loading and animation
    val car by carViewModel.carDetailState.collectAsStateWithLifecycle()

    LaunchedEffect(carId) {
        carViewModel.findById(carId)
    }

    AnimatedContent(car) { state ->
        when (state) {
            CarViewModel.CarDetailUiState.Idle, CarViewModel.CarDetailUiState.Loading -> Scaffold(
                Modifier.fillMaxSize(),
            ) {
                LoadingIndicator(Modifier.padding(it).fillMaxSize())
            }
            CarViewModel.CarDetailUiState.NotFound, CarViewModel.CarDetailUiState.Error -> CarErrorScreen(state)
            is CarViewModel.CarDetailUiState.Success -> {
                ExchangeCarOffer(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    carDetail = state.car,
                    callbacks = CarDetailCallbacks.default(state.car),
                    onExchangeClick = { exchangeViewModel.exchangeCar(state.car) },
                    modifier = modifier,
                )
            }
        }
    }
}
