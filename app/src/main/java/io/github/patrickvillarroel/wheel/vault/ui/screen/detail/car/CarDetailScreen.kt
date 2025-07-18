package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun CarDetailScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    carId: UUID,
    onEditClick: (CarItem) -> Unit,
    headerBackCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel = koinViewModel(),
) {
    val carState by carViewModel.carDetailState.collectAsStateWithLifecycle()

    LaunchedEffect(carId) {
        carViewModel.findById(carId)
    }

    AnimatedContent(carState) { state ->
        when (state) {
            is CarViewModel.CarDetailUiState.Success -> {
                val carDetail = state.car
                CarDetailContent(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    callbacks = CarDetailCallbacks(
                        carDetail = carDetail,
                        headersBackCallbacks = headerBackCallbacks,
                        onEditClick = { onEditClick(carDetail) },
                        onDeleteClick = { /* TODO add delete modal */ },
                        onFavoriteToggle = {},
                    ),
                    modifier = modifier,
                )
            }

            is CarViewModel.CarDetailUiState.Loading, CarViewModel.CarDetailUiState.Idle -> {
                Scaffold(Modifier.fillMaxSize()) {
                    LoadingIndicator(Modifier.padding(it).fillMaxSize())
                }
            }

            is CarViewModel.CarDetailUiState.Error, is CarViewModel.CarDetailUiState.NotFound -> {
                Dialog(headerBackCallbacks.onBackClick) {
                    Text(
                        text = "Something went wrong",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}
