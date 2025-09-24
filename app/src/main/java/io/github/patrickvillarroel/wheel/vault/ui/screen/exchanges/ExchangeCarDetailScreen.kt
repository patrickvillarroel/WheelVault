package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges

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
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun ExchangeCarDetailScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    carId: UUID,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel = koinViewModel(),
    exchangeViewModel: ExchangeViewModel = koinViewModel(),
) {
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
                ExchangeCarDetailContent(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    carDetail = state.car,
                    requestText = "Busco porsche rallyE color verde, si es STH mejor.",
                    callbacks = CarDetailCallbacks.default(state.car),
                    onExchangeClick = { exchangeViewModel.exchangeCar(state.car) },
                    modifier = modifier,
                )
            }
        }
    }
}
