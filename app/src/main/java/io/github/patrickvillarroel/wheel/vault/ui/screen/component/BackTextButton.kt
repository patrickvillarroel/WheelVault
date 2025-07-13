package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R

/** Thinking to use in [MenuHeader] */
@Composable
fun BackTextButton(onBack: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onBack, modifier = modifier.padding(top = 40.dp, start = 15.dp)) {
        Icon(
            Icons.AutoMirrored.Default.KeyboardArrowLeft,
            stringResource(R.string.back),
            tint = Color.Black,
        )
        Text(stringResource(R.string.back), color = Color.Black, fontWeight = FontWeight.Bold)
    }
}
