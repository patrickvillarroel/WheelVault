package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.patrickvillarroel.wheel.vault.R

@Composable
fun CarNameCard(image: Any, name: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.wrapContentWidth().aspectRatio(1f),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF25232B)),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Imagen
            val imageModifier = Modifier
                .padding(8.dp)
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
                .align(Alignment.CenterHorizontally)

            if (image is Painter) {
                Image(
                    painter = image,
                    contentDescription = stringResource(R.string.car),
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop,
                )
            } else {
                AsyncImage(
                    model = image,
                    contentDescription = stringResource(R.string.car),
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop,
                )
            }

            // Texto
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
private fun CarCardPreview() {
    CarNameCard(
        image = painterResource(R.drawable.batman_car),
        name = "Batman",
        onClick = {},
    )
}
