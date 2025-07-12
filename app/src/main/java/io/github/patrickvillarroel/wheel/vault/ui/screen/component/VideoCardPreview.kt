package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun VideoCardPreview(thumbnail: Any, onPlayClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(150.dp)
            .aspectRatio(170f / 103f)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color.Red, RoundedCornerShape(16.dp))
            .background(Color.Black)
            .aspectRatio(16f / 9f),
    ) {
        if (thumbnail is Painter) {
            Image(
                painter = thumbnail,
                contentDescription = "Video Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            AsyncImage(
                thumbnail,
                contentDescription = "Video Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        IconButton(
            onClick = onPlayClick,
            modifier = Modifier
                .align(Alignment.Center)
                .size(48.dp)
                .background(Color.White.copy(alpha = 0.6f), shape = CircleShape),
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.Black,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Preview
@Composable
private fun CardVideoPreview() {
    WheelVaultTheme {
        LazyRow(
            modifier = Modifier.padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp)),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(5) {
                VideoCardPreview(
                    thumbnail = painterResource(id = R.drawable.thumbnail_example),
                    onPlayClick = { },
                )
            }
        }
    }
}
