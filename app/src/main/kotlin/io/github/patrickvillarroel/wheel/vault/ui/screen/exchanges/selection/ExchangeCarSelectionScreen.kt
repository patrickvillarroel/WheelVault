package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.selection

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarErrorScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.selection.ExchangeCarSelectionViewModel.CarsUiState.Error
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.selection.ExchangeCarSelectionViewModel.CarsUiState.Loading
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.selection.ExchangeCarSelectionViewModel.CarsUiState.Success
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExchangeCarSelectionScreen(
    onCarClick: (CarItem) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExchangeCarSelectionViewModel = koinViewModel(),
    exchangeViewModel: io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.ExchangeViewModel = org.koin.compose.koinInject(),
) {
    val car by viewModel.carsState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchAll()
    }

    AnimatedContent(car) { state ->
        when (state) {
            Loading -> Scaffold(
                Modifier.fillMaxSize(),
            ) {
                LoadingIndicator(Modifier.padding(it).fillMaxSize())
            }

            Error -> CarErrorScreen(CarViewModel.CarDetailUiState.Error)
            is Success -> ExchangeCarSelection(
                availableCars = state.availableCars,
                carsInActiveTrades = state.carsInActiveTrades,
                onCarClick = { selectedCar ->
                    // Seleccionar el auto del usuario para ofrecer
                    exchangeViewModel.selectOwnCarForOffer(selectedCar)
                    // Navegar a la siguiente pantalla
                    onCarClick(selectedCar)
                },
                modifier = modifier,
            )
        }
    }
}
