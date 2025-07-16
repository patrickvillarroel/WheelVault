package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuButtonHeader
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun BrandHeader(
    logoAndDescription: Pair<Any, String?>,
    headerBackCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(225.dp)
            .background(
                color = Color(0xFFE42E31),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 36.dp,
                    bottomEnd = 36.dp,
                ),
            ),
    ) {
        MenuButtonHeader(headerBackCallbacks)
        val (icon, description) = logoAndDescription
        Box(Modifier.fillMaxSize().padding(10.dp)) {
            if (icon is Painter) {
                Image(
                    icon,
                    description,
                    Modifier.align(Alignment.Center).width(300.dp),
                    contentScale = ContentScale.Crop,
                )
            } else {
                AsyncImage(
                    icon,
                    description,
                    Modifier.align(Alignment.Center).width(300.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            TextButton(
                headerBackCallbacks.onBackClick,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(y = (15).dp),
            ) {
                Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, stringResource(R.string.back), tint = Color.Black)
                Text(stringResource(R.string.back), color = Color.Black, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview
@Composable
private fun BrandHeaderPreview() {
    WheelVaultTheme {
        BrandHeader(
            R.drawable.hot_wheels_logo_black to "Hot Wheels Logo",
            HeaderBackCallbacks(
                onBackClick = {},
                onProfileClick = {},
                onGarageClick = {},
                onFavoritesClick = {},
                onStatisticsClick = {},
            ),
        )
    }
}
