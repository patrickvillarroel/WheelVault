package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme
import kotlinx.coroutines.launch

@Composable
fun CarItemCard(carItem: CarItem, onFavoriteToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    var isFavorite by remember { mutableStateOf(carItem.isFavorite) }

    // Animaciones
    val starColor by animateColorAsState(
        targetValue = if (isFavorite) Color.Yellow else Color.Gray,
        animationSpec = tween(durationMillis = 1000),
    )

    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
    )

    val rotation = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(isFavorite) {
        if (isFavorite) {
            launch {
                rotation.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                )
                rotation.snapTo(0f)
            }
            launch {
                alpha.animateTo(0.5f, animationSpec = tween(150))
                alpha.animateTo(1f, animationSpec = tween(150))
            }
        }
    }

    Box(
        modifier
            .background(Color(0xFF3A3540), shape = RoundedCornerShape(16.dp))
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AsyncImage(
                model = carItem.imageUrl,
                contentDescription = carItem.name,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(8.dp)),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = carItem.name,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp,
                    )

                    IconButton(
                        onClick = {
                            isFavorite = !isFavorite
                            onFavoriteToggle(isFavorite)
                        },
                        modifier = Modifier
                            .scale(scale)
                            .graphicsLayer(
                                rotationZ = rotation.value,
                                alpha = alpha.value,
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorito",
                            tint = starColor,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.height(4.dp), color = Color(0xFFE42E31))

                Text(
                    text = "Año: ${carItem.year}",
                    color = Color.White,
                    fontSize = 13.sp,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Fabricante: ${carItem.manufacturer}",
                    color = Color.White,
                    fontSize = 13.sp,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Cantidad: ${carItem.quantity}",
                    color = Color.White,
                    fontSize = 13.sp,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CardItemPreview() {
    val car = remember {
        CarItem(
            name = "Ford Mustang GTD",
            year = 2025,
            manufacturer = "HotWheels",
            quantity = 2,
            imageUrl = "https://tse1.mm.bing.net/th/id/OIP.zfsbW7lEIwYgeUt7Fd1knwHaHg?rs=1&pid=ImgDetMain&o=7&rm=3",
            isFavorite = true,
        )
    }

    WheelVaultTheme {
        CarItemCard(carItem = car, onFavoriteToggle = {}, modifier = Modifier.padding(16.dp))
    }
}
