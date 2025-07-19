package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
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
import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun BrandDetailScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    brandId: UUID,
    onAddClick: () -> Unit,
    onCarClick: (UUID) -> Unit,
    headerBackCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
    brandViewModel: BrandViewModel = koinViewModel(),
) {
    val brandState by brandViewModel.brandDetailsState.collectAsStateWithLifecycle()

    LaunchedEffect(brandId) {
        brandViewModel.findById(brandId)
    }

    Crossfade(brandState, label = "BrandDetail") { state ->
        when (state) {
            is BrandViewModel.BrandDetailsUiState.Success -> BrandDetailContent(
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                brandDetail = BrandDetail(
                    brand = state.brand,
                    carCollection = state.cars,
                    onAddClick = onAddClick,
                    onCarClick = onCarClick,
                    onFavoriteToggle = { _, _ -> },
                    headerBackCallbacks = headerBackCallbacks,
                    animationKey = "brand-$brandId",
                ),
                modifier = modifier,
            )

            BrandViewModel.BrandDetailsUiState.Loading, BrandViewModel.BrandDetailsUiState.Idle -> {
                Scaffold(Modifier.fillMaxSize()) {
                    LoadingIndicator(Modifier.padding(it).fillMaxSize())
                }
            }

            BrandViewModel.BrandDetailsUiState.Error, BrandViewModel.BrandDetailsUiState.NotFound -> {
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
