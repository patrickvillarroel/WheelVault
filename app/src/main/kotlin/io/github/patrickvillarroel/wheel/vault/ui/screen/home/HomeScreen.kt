package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import android.content.Intent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    callbacks: HomeNavCallbacks,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val brands by viewModel.brandsImages.collectAsStateWithLifecycle()
    val recentCars by viewModel.recentCarImages.collectAsStateWithLifecycle()
    val news by viewModel.news.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchAllBrands()
        viewModel.fetchNews()
        viewModel.fetchAllCarImages()
    }

    HomeContent(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        brands = brands,
        news = news,
        recentCars = recentCars,
        info = HomeCallbacks(
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
                viewModel.fetchAllBrands(true)
                viewModel.fetchNews(true)
                viewModel.fetchAllCarImages(true)
            },
            headersCallbacks = callbacks.headerCallbacks,
        ),
        modifier = modifier,
    )
}
