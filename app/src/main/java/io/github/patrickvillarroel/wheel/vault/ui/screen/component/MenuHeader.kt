package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun MenuHeader(
    onProfileClick: () -> Unit,
    onGarageClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondRow: @Composable BoxScope.() -> Unit = {
        TextButton(onClick = {}, modifier = Modifier.padding(top = 40.dp, start = 15.dp)) {
            Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, stringResource(R.string.back), tint = Color.Black)
            Text(stringResource(R.string.back), color = Color.Black)
        }
    },
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = Color(0xFFE42E31),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 36.dp,
                    bottomEnd = 36.dp,
                ),
            ).windowInsetsPadding(WindowInsets.statusBars),
    ) {
        MenuButtonHeader(
            onProfileClick = onProfileClick,
            onGarageClick = onGarageClick,
            onFavoritesClick = onFavoritesClick,
            onStatisticsClick = onStatisticsClick,
        ) {
            Text(
                stringResource(R.string.collectors_project),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        secondRow()
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HeaderBrushPreview() {
    WheelVaultTheme {
        MenuHeader(onProfileClick = {}, onGarageClick = {}, onFavoritesClick = {}, onStatisticsClick = {})
    }
}
