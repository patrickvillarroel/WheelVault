package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.TradeProposal
import kotlin.time.Instant

@Composable
fun ExchangeHistoryCard(trade: TradeProposal.CurrentTradeStatus, modifier: Modifier = Modifier) {
    val isSuccessful = trade.currentStatus == TradeProposal.TradeEventType.ACCEPTED ||
        trade.currentStatus == TradeProposal.TradeEventType.COMPLETED

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSuccessful) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (isSuccessful) {
                            Icons.Filled.CheckCircle
                        } else {
                            Icons.Filled.Cancel
                        },
                        contentDescription = null,
                        tint = if (isSuccessful) {
                            Color(0xFF4CAF50)
                        } else {
                            Color(0xFFE42E31)
                        },
                        modifier = Modifier.size(24.dp),
                    )

                    Text(
                        text = getStatusText(trade.currentStatus),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSuccessful) {
                            Color(0xFF4CAF50)
                        } else {
                            Color(0xFFE42E31)
                        },
                    )
                }

                Text(
                    text = formatDate(trade.lastUpdated),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Thumbnail del auto ofrecido
                CarThumbnail(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Filled.SwapHoriz,
                    contentDescription = stringResource(R.string.exchange),
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                // Thumbnail del auto solicitado
                CarThumbnail(modifier = Modifier.weight(1f))
            }

            if (!trade.lastMessage.isNullOrBlank()) {
                Text(
                    text = trade.lastMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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
        AsyncImage(
            model = null,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(80.dp),
        )
    }
}

@Composable
private fun getStatusText(status: TradeProposal.TradeEventType): String = when (status) {
    TradeProposal.TradeEventType.ACCEPTED -> stringResource(R.string.exchange_status_accepted)
    TradeProposal.TradeEventType.REJECTED -> stringResource(R.string.exchange_status_rejected)
    TradeProposal.TradeEventType.CANCELLED -> stringResource(R.string.exchange_status_cancelled)
    TradeProposal.TradeEventType.COMPLETED -> stringResource(R.string.exchange_status_completed)
    else -> stringResource(R.string.exchange_status_proposed)
}

private fun formatDate(instant: Instant): String = instant.toString().take(10)
