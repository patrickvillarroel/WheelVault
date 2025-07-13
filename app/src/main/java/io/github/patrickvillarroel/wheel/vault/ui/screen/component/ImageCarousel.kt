package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ImageCarousel(images: List<String>, modifier: Modifier = Modifier) {
    val state = rememberCarouselState { images.size }

    HorizontalCenteredHeroCarousel(
        state = state,
        minSmallItemWidth = 5.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(5.dp),
    ) { page ->
        Box(Modifier.background(color = Color(0xFF2C2930), shape = RoundedCornerShape(size = 15.dp))) {
            AsyncImage(
                model = images[page],
                contentDescription = "Image $page",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            )
        }
    }
}

@Composable
fun HeroImageCarousel(images: List<String>, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState { images.size }

    HorizontalPager(
        state = pagerState,
        pageSpacing = 16.dp,
        contentPadding = PaddingValues(horizontal = 32.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 5.dp),
    ) { page ->
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            AsyncImage(
                model = images[page],
                contentDescription = "Imagen $page",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
