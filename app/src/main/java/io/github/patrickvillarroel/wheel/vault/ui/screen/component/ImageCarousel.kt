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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patrickvillarroel.wheel.vault.R

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
                images[page],
                stringResource(R.string.image_of, page),
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
        val image = images.elementAt(page)
        val page = page + 1
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF7F7F7F)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            when (image) {
                is Painter -> {
                    Image(
                        image,
                        stringResource(R.string.image_of, page),
                        Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }

                is Bitmap -> {
                    Image(
                        image.asImageBitmap(),
                        stringResource(R.string.image_of, page),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }

                is ImageVector -> {
                    Image(
                        image,
                        stringResource(R.string.image_of, page),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }

                else -> {
                    AsyncImage(
                        model = image,
                        stringResource(R.string.image_of, page),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
