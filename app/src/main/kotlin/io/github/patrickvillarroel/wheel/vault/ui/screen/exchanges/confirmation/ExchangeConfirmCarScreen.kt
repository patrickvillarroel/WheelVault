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
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarDetailCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarErrorScreen
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.ExchangeViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid

@Composable
fun ExchangeConfirmCarScreen(
    requestCarId: Uuid,
    modifier: Modifier = Modifier,
    exchangeViewModel: ExchangeViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
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

            ExchangeViewModel.ExchangeConfirmUiState.Accepted -> ExchangeResultScreen(
                isAccepted = true,
                onNavigateBack = onNavigateBack,
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
                onNavigateBack = onNavigateBack,
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
                        stringResource(R.string.completed)
                    } else {
                        stringResource(R.string.cancel)
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = if (isAccepted) {
                        "El intercambio ha sido aceptado exitosamente. El otro coleccionista será notificado."
                    } else {
                        "El intercambio ha sido rechazado. El otro coleccionista será notificado."
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
