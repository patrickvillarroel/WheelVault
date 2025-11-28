package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R

@Composable
fun TradeAvailabilityButton(
    isAvailableForTrade: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (isAvailableForTrade) 1.05f else 1f,
        animationSpec = tween(300),
        label = "button_scale",
    )

    val containerColor by animateColorAsState(
        targetValue = if (isAvailableForTrade) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(300),
        label = "container_color",
    )

    val contentColor by animateColorAsState(
        targetValue = if (isAvailableForTrade) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300),
        label = "content_color",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        Box(contentAlignment = Alignment.Center) {
            FilledIconButton(
                onClick = onToggle,
                modifier = Modifier
                    .size(65.dp)
                    .scale(scale)
                    .then(
                        if (isAvailableForTrade) {
                            Modifier.border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                            )
                        } else {
                            Modifier
                        },
                    ),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                ),
            ) {
                AnimatedContent(
                    targetState = isAvailableForTrade,
                    transitionSpec = {
                        scaleIn(tween(200)) togetherWith scaleOut(tween(200))
                    },
                    label = "icon_animation",
                ) { isAvailable ->
                    if (isAvailable) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = stringResource(R.string.unmark_for_trading),
                            modifier = Modifier.size(32.dp),
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.SwapHoriz,
                            contentDescription = stringResource(R.string.mark_for_trading),
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            }

            // Badge indicator cuando está disponible
            if (isAvailableForTrade) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.TopEnd)
                        .background(Color(0xFF4CAF50), CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                )
            }
        }

        Text(
            text = if (isAvailableForTrade) {
                stringResource(R.string.unmark_for_trading)
            } else {
                stringResource(R.string.mark_for_trading)
            },
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}
