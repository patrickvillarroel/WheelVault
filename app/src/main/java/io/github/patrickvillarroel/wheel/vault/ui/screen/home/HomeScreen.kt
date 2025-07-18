package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    callbacks: HomeNavCallbacks,
    modifier: Modifier = Modifier,
    brandViewModel: BrandViewModel = koinViewModel(),
    carViewModel: CarViewModel = koinViewModel(),
) {
    val brands by brandViewModel.brandsImages.collectAsStateWithLifecycle()
    val recentCars by carViewModel.recentCarsImages.collectAsStateWithLifecycle()
    val news = remember { listOf(R.drawable.thumbnail_example) }

    HomeContent(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        info = HomeCallbacks(
            // TODO, maybe need a loading status and stuffs
            brands = brands,
            news = news,
            recentCars = recentCars,
            onAddClick = callbacks.onAddClick,
            onSearchClick = callbacks.onSearchClick,
            onBrandClick = callbacks.onBrandClick,
            onNewsClick = { /* TODO play the video in full screen */ },
            onCarClick = callbacks.onCarClick,
            onRefresh = {
                brandViewModel.fetchAll()
                carViewModel.fetchAll()
            },
            headerCallbacks = callbacks.headerCallbacks,
        ),
        modifier = modifier,
    )
}
