package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ImageCarousel(images: List<Any>, modifier: Modifier = Modifier) {
    val state = rememberCarouselState { images.size }

    HorizontalCenteredHeroCarousel(
        state = state,
        minSmallItemWidth = 5.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(5.dp),
    ) { page ->
        Card(shape = RoundedCornerShape(size = 15.dp), modifier = Modifier.fillMaxSize()) {
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
fun HeroImageCarousel(images: Set<Any>, modifier: Modifier = Modifier) {
    HorizontalPager(
        state = rememberPagerState { images.size },
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFF7F7F7F)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            val image = images.elementAt(page)
            val page = page + 1
            when (image) {
                is Painter -> {
                    Image(
                        image,
                        contentDescription = "Imagen $page",
                        Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }

                is Bitmap -> {
                    Image(
                        image.asImageBitmap(),
                        contentDescription = "Imagen $page",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }

                is ImageVector -> {
                    Image(
                        image,
                        contentDescription = "Imagen $page",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }

                else -> {
                    AsyncImage(
                        model = image,
                        contentDescription = "Imagen $page",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
