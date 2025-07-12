package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patrickvillarroel.wheel.vault.R

@Composable
fun BrandCard(logo: Any, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.size(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE53935)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (logo is Painter) {
                Image(
                    painter = logo,
                    contentDescription = stringResource(R.string.car),
                    modifier = Modifier.size(40.dp),
                    contentScale = ContentScale.Fit,
                )
            } else {
                AsyncImage(
                    logo,
                    contentDescription = stringResource(R.string.car),
                    modifier = Modifier.size(40.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}
