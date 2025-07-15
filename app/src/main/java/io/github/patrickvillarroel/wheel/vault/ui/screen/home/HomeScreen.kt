package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.patrickvillarroel.wheel.vault.R

@Composable
fun HomeScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    callbacks: HomeNavCallbacks,
    modifier: Modifier = Modifier,
) {
    // TODO use VM or something
    val brands = remember { listOf(1 to R.drawable.hot_wheels_logo_black) }
    val news = remember { listOf(R.drawable.thumbnail_example) }
    val recentCars = remember { listOf(1 to R.drawable.batman_car) }

    HomeContent(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        info = HomeCallbacks(
            brands = brands,
            news = news,
            recentCars = recentCars,
            onAddClick = callbacks.onAddClick,
            onSearchClick = callbacks.onSearchClick,
            onBrandClick = callbacks.onBrandClick,
            onNewsClick = { /* TODO play the video in full screen */ },
            onCarClick = callbacks.onCarClick,
            headerCallbacks = callbacks.headerCallbacks,
        ),
        modifier = modifier,
    )
}
