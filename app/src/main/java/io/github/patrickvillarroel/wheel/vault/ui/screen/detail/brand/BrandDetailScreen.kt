package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.patrickvillarroel.wheel.vault.R
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
                Scaffold(Modifier.fillMaxSize()) {
                    Column(
                        Modifier.padding(it).fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Image(
                            painterResource(
                                if (state is BrandViewModel.BrandDetailsUiState.Error) {
                                    R.drawable.error
                                } else {
                                    R.drawable.no_data
                                },
                            ),
                            null,
                            Modifier.padding(16.dp).fillMaxWidth(0.8f),
                        )
                        Text(
                            stringResource(R.string.error_loading_of, stringResource(R.string.brands)),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}
