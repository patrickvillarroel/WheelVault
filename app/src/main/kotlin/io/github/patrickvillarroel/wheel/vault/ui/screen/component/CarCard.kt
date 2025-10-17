package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patrickvillarroel.wheel.vault.R

@Composable
fun CarCard(image: Any, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .size(80.dp)
            .border(2.dp, Color.Red, RoundedCornerShape(12.dp)),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        if (image is Painter) {
            Image(
                painter = image,
                contentDescription = stringResource(R.string.car),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            AsyncImage(
                model = image,
                contentDescription = stringResource(R.string.car),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Preview
@Composable
private fun CarCardPreview() {
    CarCard(
        image = painterResource(R.drawable.batman_car),
        onClick = {},
    )
}
