package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun FavoriteIcon(isFavorite: Boolean, onFavoriteToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    var isFavorite by remember { mutableStateOf(isFavorite) }

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

    IconButton(
        onClick = {
            isFavorite = !isFavorite
            onFavoriteToggle(isFavorite)
        },
        modifier = modifier
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
