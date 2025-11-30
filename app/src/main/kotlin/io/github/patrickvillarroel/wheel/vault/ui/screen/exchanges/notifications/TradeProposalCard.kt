package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.model.TradeProposal

@Composable
fun TradeProposalCard(
    notification: ExchangeNotificationsViewModel.TradeNotification,
    isReceived: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isReceived) {
                        stringResource(R.string.received_proposals)
                    } else {
                        stringResource(R.string.sent_proposals)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )

                if (notification.trade.isExpired) {
                    TradeStatusBadge(status = TradeProposal.TradeEventType.EXPIRED)
                } else {
                    TradeStatusBadge(status = notification.trade.currentStatus)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Auto ofrecido con info
                CarThumbnailWithInfo(
                    car = notification.offeredCar,
                    label = "Ofrece",
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Filled.SwapHoriz,
                    contentDescription = stringResource(R.string.exchange),
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                // Auto solicitado con info
                CarThumbnailWithInfo(
                    car = notification.requestedCar,
                    label = "Solicita",
                    modifier = Modifier.weight(1f)
                )
            }

            if (!notification.trade.initialMessage.isNullOrBlank()) {
                Text(
                    text = notification.trade.initialMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = formatDate(notification.trade.proposedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (notification.trade.isPending && notification.trade.originalExpiresAt != null) {
                    val now = kotlin.time.Clock.System.now()
                    val timeLeft = notification.trade.originalExpiresAt - now
                    if (timeLeft.isPositive()) {
                        val hoursLeft = timeLeft.inWholeHours
                        Text(
                            text = "Expira en ${hoursLeft}h",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (hoursLeft < 24) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = if (hoursLeft < 24) FontWeight.Bold else FontWeight.Normal,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CarThumbnailWithInfo(
    car: CarItem,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = car.imageUrl,
                    contentDescription = "${car.brand} ${car.model}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        Text(
            text = "${car.brand} ${car.model}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = car.year.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TradeStatusBadge(status: TradeProposal.TradeEventType) {
    val (text, color) = when (status) {
        TradeProposal.TradeEventType.PROPOSED -> stringResource(R.string.exchange_status_proposed) to MaterialTheme.colorScheme.tertiary
        TradeProposal.TradeEventType.ACCEPTED -> stringResource(R.string.exchange_status_accepted) to MaterialTheme.colorScheme.primary
        TradeProposal.TradeEventType.REJECTED -> stringResource(R.string.exchange_status_rejected) to MaterialTheme.colorScheme.error
        TradeProposal.TradeEventType.CANCELLED -> stringResource(R.string.exchange_status_cancelled) to MaterialTheme.colorScheme.outline
        TradeProposal.TradeEventType.COMPLETED -> stringResource(R.string.exchange_status_completed) to MaterialTheme.colorScheme.primary
        TradeProposal.TradeEventType.EXPIRED -> "Expirada" to MaterialTheme.colorScheme.error
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f),
        ),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun formatDate(instant: kotlin.time.Instant): String {
    // Implementación simplificada - deberías usar una librería de formateo de fechas
    return instant.toString().take(10)
}
