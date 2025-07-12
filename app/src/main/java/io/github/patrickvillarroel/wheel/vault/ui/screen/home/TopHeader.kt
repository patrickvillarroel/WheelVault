package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuButtonHeader
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun TopHeader(
    onProfileClick: () -> Unit,
    onGarageClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Encabezado con iconos y menu
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE42E31),
                        Color(0xFF7E191B),
                    ),
                    startY = 0f,
                    endY = 1400f,
                ),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 36.dp,
                    bottomEnd = 36.dp,
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
        ) {
            MenuButtonHeader(
                onProfileClick = onProfileClick,
                onGarageClick = onGarageClick,
                onFavoritesClick = onFavoritesClick,
                onStatisticsClick = onStatisticsClick,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.collectors_project_lines),
                color = Color.White,
                fontSize = 70.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 52.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Imagen del auto principal
        Image(
            painter = painterResource(id = R.drawable.header_car_fixed),
            contentDescription = stringResource(R.string.car),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(top = 170.dp)
                .height(259.dp)
                .align(Alignment.BottomEnd),
        )
    }
}

@Composable
private fun TopHeaderWithBrush(modifier: Modifier = Modifier) {
    // Encabezado con imagen
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE53935), Color(0xFF1A1A1A)),
                    startY = 0f,
                    endY = 600f,
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = Color.White,
                )
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.White,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Collectors\nProject",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 36.sp,
            )
        }

        // Imagen del auto principal
        Image(
            painter = painterResource(id = R.drawable.header_car),
            contentDescription = "Carro destacado",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp)
                .height(205.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HeaderPreview() {
    WheelVaultTheme {
        TopHeader(onProfileClick = {}, onGarageClick = {}, onFavoritesClick = {}, onStatisticsClick = {})
    }
}

@Preview
@Composable
private fun HeaderBrushPreview() {
    WheelVaultTheme {
        TopHeaderWithBrush()
    }
}
