package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.selection

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarErrorScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExchangeCarSelectionScreen(
    modifier: Modifier = Modifier,
    onCarClick: (CarItem) -> Unit,
    carViewModel: CarViewModel = koinViewModel(),
) {
    // TODO connect exchange VM with cars available for exchange
    val car by carViewModel.carsState.collectAsStateWithLifecycle()

    AnimatedContent(car) { state ->
        when (state) {
            CarViewModel.CarsUiState.Loading -> Scaffold(
                Modifier.fillMaxSize(),
            ) {
                LoadingIndicator(Modifier.padding(it).fillMaxSize())
            }

            CarViewModel.CarsUiState.Error -> CarErrorScreen(CarViewModel.CarDetailUiState.Error)
            is CarViewModel.CarsUiState.Success -> ExchangeCarSelection(
                state.cars,
                onCarClick = onCarClick,
                modifier = modifier,
            )
        }
    }
}
