package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.notifications

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.model.TradeProposal
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BackTextButton
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuHeader
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid

@Composable
fun TradeProposalDetailScreen(
    tradeGroupId: Uuid,
    headerCallbacks: HeaderBackCallbacks,
    onTradeActionCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TradeProposalDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(tradeGroupId) {
        viewModel.loadTradeProposal(tradeGroupId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MenuHeader(headerCallbacks) {
                BackTextButton(onBack = headerCallbacks.onBackClick)
            }
        },
    ) { paddingValues ->
        AnimatedContent(
            targetState = uiState,
            label = "trade_detail_content",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) { state ->
            when (state) {
                is TradeProposalDetailViewModel.TradeDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoadingIndicator()
                    }
                }

                is TradeProposalDetailViewModel.TradeDetailUiState.Success -> {
                    TradeProposalDetailContent(
                        trade = state.trade,
                        offeredCar = state.offeredCar,
                        requestedCar = state.requestedCar,
                        isReceived = state.isReceived,
                        onAccept = {
                            viewModel.acceptTrade(tradeGroupId)
                        },
                        onReject = {
                            viewModel.rejectTrade(tradeGroupId)
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                is TradeProposalDetailViewModel.TradeDetailUiState.ActionCompleted -> {
                    LaunchedEffect(Unit) {
                        onTradeActionCompleted()
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = when (state.action) {
                                TradeProposalDetailViewModel.TradeAction.ACCEPTED -> stringResource(R.string.exchange_status_accepted)
                                TradeProposalDetailViewModel.TradeAction.REJECTED -> stringResource(R.string.exchange_status_rejected)
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                is TradeProposalDetailViewModel.TradeDetailUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.error_loading_of, "propuesta"),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TradeProposalDetailContent(
    trade: TradeProposal.CurrentTradeStatus,
    offeredCar: CarItem,
    requestedCar: CarItem,
    isReceived: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = if (isReceived) "Propuesta Recibida" else "Propuesta Enviada",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        if (trade.isPending && trade.originalExpiresAt != null) {
            val now = kotlin.time.Clock.System.now()
            val timeLeft = trade.originalExpiresAt - now
            if (timeLeft.isPositive()) {
                val hoursLeft = timeLeft.inWholeHours
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    ),
                ) {
                    Text(
                        text = "Expira en $hoursLeft horas",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        if (trade.isExpired) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ),
            ) {
                Text(
                    text = "Esta propuesta ha expirado",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CarDetailCard(
                car = offeredCar,
                label = "Auto ofrecido",
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Filled.SwapHoriz,
                contentDescription = stringResource(R.string.exchange),
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary,
            )

            CarDetailCard(
                car = requestedCar,
                label = "Auto solicitado",
                modifier = Modifier.weight(1f)
            )
        }

        if (!trade.initialMessage.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Mensaje",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = trade.initialMessage,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        if (isReceived && trade.isPending && !trade.isExpired) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Rechazar")
                }

                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Aceptar")
                }
            }
        }

        Text(
            text = "Propuesta realizada el ${formatDate(trade.proposedAt)}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CarDetailCard(
    car: CarItem,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp)),
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${car.brand} ${car.model}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Text(
                text = car.year.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatDate(instant: kotlin.time.Instant): String {
    return instant.toString().take(10)
}
