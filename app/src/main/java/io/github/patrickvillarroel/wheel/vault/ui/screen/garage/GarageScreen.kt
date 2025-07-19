package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun GarageScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onHomeClick: () -> Unit,
    onAddClick: () -> Unit,
    onCarClick: (UUID) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CarViewModel = koinViewModel(),
) {
    val carsState by viewModel.carsState.collectAsStateWithLifecycle()

    val carResults = when (carsState) {
        is CarViewModel.CarsUiState.Success -> (carsState as CarViewModel.CarsUiState.Success).cars
        else -> emptyList()
    }

    GarageContent(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        carResults = carResults,
        callbacks = GarageCallbacks(
            onHomeClick = onHomeClick,
            onSearch = { /* No se aun como hacer esto */ },
            onAddClick = onAddClick,
            onCarClick = { carItem -> onCarClick(carItem.id) },
            onToggleFavorite = { carItem, newValue ->
                viewModel.save(carItem.copy(isFavorite = newValue).toPartial())
            },
            headersCallbacks = HeaderCallbacks(
                onProfileClick = onProfileClick,
                onGarageClick = {},
                onFavoritesClick = {},
                onStatisticsClick = {},
            ),
        ),
        modifier = modifier,
    )
}
