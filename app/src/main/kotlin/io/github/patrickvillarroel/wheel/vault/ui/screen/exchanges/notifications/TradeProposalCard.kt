package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.TradeProposal

@Composable
fun TradeProposalCard(
    trade: TradeProposal.CurrentTradeStatus,
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

                TradeStatusBadge(status = trade.currentStatus)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Imagen del auto ofrecido (simplificado - necesitarás cargar las imágenes reales)
                CarThumbnail(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Filled.SwapHoriz,
                    contentDescription = stringResource(R.string.exchange),
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                // Imagen del auto solicitado
                CarThumbnail(modifier = Modifier.weight(1f))
            }

            if (!trade.initialMessage.isNullOrBlank()) {
                Text(
                    text = trade.initialMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
            }

            Text(
                text = formatDate(trade.proposedAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CarThumbnail(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        // Aquí irá la imagen del auto cuando se implemente
        AsyncImage(
            model = null,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(80.dp),
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
        TradeProposal.TradeEventType.EXPIRED -> stringResource(R.string.exchange_status_cancelled) to MaterialTheme.colorScheme.outline
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
