package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.confirmation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BackTextButton
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuHeader
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.component.ConfirmationDialog
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

private val logger = Logger.withTag("ExchangeConfirmCar")

@Composable
fun ExchangeConfirmCar(
    offeredCar: CarItem,
    requestedCar: CarItem,
    callbacks: HeaderBackCallbacks,
    onAcceptClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    isRespondingToOffer: Boolean = false,
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var showConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    val tabs = listOf(stringResource(R.string.offered_car), stringResource(R.string.requested_car))
    // Log para debugging
    logger.d {
        "offeredCar: id=${offeredCar.id}, brand=${offeredCar.brand}, model=${offeredCar.model}\n" +
            "requestedCar: id=${requestedCar.id}, brand=${requestedCar.brand}, model=${requestedCar.model}"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MenuHeader(callbacks) {
                BackTextButton(onBack = callbacks.onBackClick)
            }
        },
        floatingActionButton = {
            ExchangeActionButtons(
                onAcceptClick = {
                    logger.d { "Accept clicked - showing confirmation dialog" }
                    @Suppress("ASSIGNED_VALUE_IS_NEVER_READ")
                    showConfirmationDialog = true
                },
                onCancelClick = {
                    logger.d { "Cancel clicked" }
                    @Suppress("ASSIGNED_VALUE_IS_NEVER_READ")
                    showConfirmationDialog = false
                    onCancelClick()
                },
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            item {
                ExchangeCarHeader(
                    offeredCar.images.firstOrNull(),
                    requestedCar.images.firstOrNull(),
                    Modifier.padding(top = 16.dp),
                )
            }

            if (!message.isNullOrBlank()) {
                item {
                    ExchangeMessageCard(message = message, Modifier.padding(vertical = 8.dp))
                }
            }

            item {
                ExchangeTabs(
                    tabs = tabs,
                    selectedTabIndex = selectedTabIndex,
                    onTabClick = { index -> selectedTabIndex = index },
                )
            }

            // Usar AnimatedContent para cambiar entre los dos autos
            item(key = "car_details_animated") {
                AnimatedContent(
                    targetState = selectedTabIndex,
                    label = "car_details_transition",
                ) { tabIndex ->
                    val carToShow = if (tabIndex == 0) offeredCar else requestedCar
                    logger.d {
                        "Showing tab $tabIndex - car: id=${carToShow.id}, brand=${carToShow.brand}, model=${carToShow.model}"
                    }
                    Column {
                        // Brand
                        Text(stringResource(R.string.brand), style = MaterialTheme.typography.labelLarge)
                        Text(carToShow.brand, style = MaterialTheme.typography.bodyLarge)
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))

                        // Model
                        Text(stringResource(R.string.model), style = MaterialTheme.typography.labelLarge)
                        Text(carToShow.model, style = MaterialTheme.typography.bodyLarge)
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))

                        // Year
                        Text(stringResource(R.string.year), style = MaterialTheme.typography.labelLarge)
                        Text(carToShow.year.toString(), style = MaterialTheme.typography.bodyLarge)
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))

                        // Manufacturer
                        Text(stringResource(R.string.manufacturer), style = MaterialTheme.typography.labelLarge)
                        Text(carToShow.manufacturer, style = MaterialTheme.typography.bodyLarge)
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))

                        // Description (optional)
                        if (!carToShow.description.isNullOrBlank()) {
                            Text(stringResource(R.string.description), style = MaterialTheme.typography.labelLarge)
                            Text(carToShow.description, style = MaterialTheme.typography.bodyLarge)
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                        }

                        // Category (optional)
                        if (!carToShow.category.isNullOrBlank()) {
                            Text(stringResource(R.string.category), style = MaterialTheme.typography.labelLarge)
                            Text(carToShow.category, style = MaterialTheme.typography.bodyLarge)
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }

            item {
                Box(Modifier.height(100.dp))
            }
        }
        if (showConfirmationDialog) {
            ConfirmationDialog(
                message = stringResource(
                    id = if (isRespondingToOffer) {
                        R.string.exchange_accept_confirm_message
                    } else {
                        R.string.exchange_create_confirm_message
                    },
                ),
                onDismissRequest = {
                    logger.d { "Confirmation dialog dismissed" }
                    @Suppress("ASSIGNED_VALUE_IS_NEVER_READ")
                    showConfirmationDialog = false
                },
                onConfirm = {
                    logger.d { "Confirmation dialog confirmed - calling onAcceptClick()" }
                    @Suppress("ASSIGNED_VALUE_IS_NEVER_READ")
                    showConfirmationDialog = false
                    onAcceptClick()
                },
            )
        }
    }
}

@Composable
private fun ExchangeMessageCard(message: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(R.string.exchange_message_label),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun ExchangeCarHeader(offeredCarImage: Any?, requestedCarImage: Any?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CarImage(offeredCarImage, Modifier.weight(1f))
            Icon(
                Icons.Filled.ChangeCircle,
                contentDescription = stringResource(R.string.exchange),
                tint = Color.White,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFE42E31)),
            )
            CarImage(requestedCarImage, Modifier.weight(1f))
        }
    }
}

@Composable
private fun CarImage(imageUrl: Any?, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .padding(8.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(8.dp)),
    ) {
        if (imageUrl is Painter) {
            Image(
                imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun ExchangeTabs(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Color.Unspecified,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(selectedTabIndex),
                    color = Color(0xFFE42E31),
                )
            },
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabClick(index) },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.surfaceTint,
                    unselectedContentColor = Color.Gray,
                )
            }
        }
        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp,
        )
    }
}

@Composable
private fun ExchangeActionButtons(onAcceptClick: () -> Unit, onCancelClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ActionButton(
            onClick = {
                logger.d(tag = "ExchangeActionButtons") { "Cancel button clicked" }
                onCancelClick()
            },
            icon = Icons.Filled.Cancel,
            text = stringResource(R.string.cancel),
            iconColor = Color.Black,
            filledButtonColor = Color(0xFFE42E31),
        )

        ActionButton(
            onClick = {
                logger.d(tag = "ExchangeActionButtons") { "Accept/Confirm button clicked" }
                onAcceptClick()
            },
            icon = Icons.Filled.Sync,
            text = stringResource(R.string.confirm),
            iconColor = Color(0xFFE42E31),
            filledButtonColor = Color.Transparent,
            borderButton = true,
        )
    }
}

@Composable
private fun ActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
    iconColor: Color,
    filledButtonColor: Color,
    modifier: Modifier = Modifier,
    borderButton: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(65.dp).let {
                if (borderButton) {
                    return@let it.border(
                        width = 2.dp,
                        color = Color(0xFFE42E31),
                        shape = RoundedCornerShape(size = 50.dp),
                    )
                }
                it
            },
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = filledButtonColor),
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(40.dp),
            )
        }
        Text(
            text,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@PreviewLightDark
@Composable
private fun ExchangeConfirmCarPreview() {
    val offeredCar = CarItem(
        model = "Offered Model",
        year = 2022,
        manufacturer = "Offered Manufacturer",
        brand = "Offered Brand",
        quantity = 1,
        isFavorite = false,
        description = "Offered car description",
        category = "Offered car category",
    )
    val requestedCar = CarItem(
        model = "Requested Model",
        year = 2023,
        manufacturer = "Requested Manufacturer",
        brand = "Requested Brand",
        quantity = 1,
        isFavorite = true,
        description = "Requested car description",
        category = "Requested car category",
    )
    val callbacks = HeaderBackCallbacks.default

    WheelVaultTheme {
        ExchangeConfirmCar(
            offeredCar = offeredCar,
            requestedCar = requestedCar,
            callbacks = callbacks,
            onAcceptClick = {},
            onCancelClick = {},
        )
    }
}
