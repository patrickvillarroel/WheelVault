package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import android.content.Intent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val brands by brandViewModel.brandsImages.collectAsStateWithLifecycle()
    val recentCars by carViewModel.recentCarsImages.collectAsStateWithLifecycle()
    val news by homeViewModel.news.collectAsStateWithLifecycle()

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
            onNewsClick = { video ->
                val rawIntent = Intent(Intent.ACTION_VIEW, video.link.toUri())
                val youtubeIntent = rawIntent.clone() as Intent
                youtubeIntent.setPackage("com.google.android.youtube") // Abre la app youtube si está instalada

                if (youtubeIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(youtubeIntent)
                } else {
                    // Abrir en navegador si no hay app de YouTube
                    context.startActivity(rawIntent)
                }
            },
            onCarClick = callbacks.onCarClick,
            onRefresh = {
                brandViewModel.fetchAll()
                carViewModel.fetchAll()
                homeViewModel.fetchNews()
            },
            headerCallbacks = callbacks.headerCallbacks,
        ),
        modifier = modifier,
    )
}
