package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    headerCallbacks: HeaderCallbacks,
    modifier: Modifier = Modifier,
    showNotifications: Boolean = false,
    secondRow: @Composable BoxScope.() -> Unit = {},
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
                    bottomStart = 30.dp,
                    bottomEnd = 30.dp,
                ),
            ).windowInsetsPadding(WindowInsets.statusBars),
    ) {
        MenuButtonHeader(
            headerCallbacks = headerCallbacks,
            showNotifications = showNotifications,
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
        MenuHeader(HeaderCallbacks.default)
    }
}
