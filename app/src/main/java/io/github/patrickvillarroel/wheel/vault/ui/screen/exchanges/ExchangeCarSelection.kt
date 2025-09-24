package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem.Companion.EmptyImage
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.CarItemCard
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuButtonHeader

@Composable
fun ExchangeCarSelection(carResults: List<CarItem>, onCarClick: (CarItem) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CarSelectionHeader(onHomeClick = {}, headerCallbacks = HeaderCallbacks.default)
        },
    ) { paddingValues ->
        LazyColumn(Modifier.padding(paddingValues).fillMaxSize().padding(start = 15.dp, top = 7.dp, end = 15.dp)) {
            items(carResults, key = { it.id }) { item ->
                CarItemCard(
                    carItem = item,
                    onClick = { onCarClick(item) },
                    onFavoriteToggle = {},
                    modifier = Modifier.padding(3.dp),
                    favoriteIcon = false,
                )
            }
        }
    }
}

@Composable
private fun CarSelectionHeader(
    onHomeClick: () -> Unit,
    headerCallbacks: HeaderCallbacks,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = Color(0xFFE42E31),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 30.dp,
                    bottomEnd = 30.dp,
                ),
            ).windowInsetsPadding(WindowInsets.statusBars),
    ) {
        MenuButtonHeader(headerCallbacks)
        Text(
            stringResource(R.string.select_car_to_exchange),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 70.dp, end = 70.dp, top = 40.dp, bottom = 40.dp).fillMaxWidth(),
        )
        AssistChip(
            onClick = onHomeClick,
            label = { Icon(Icons.Filled.Home, stringResource(R.string.home), tint = Color.White) },
            colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF1D1B20)),
            border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = Color(0xFF1D1B20)),
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 30.dp),
        )
    }
}

@Preview
@Composable
private fun ExchangeCarSelectionPreview() {
    val carResults = List(10) {
        CarItem(
            model = "F-150",
            year = 2023,
            manufacturer = "Ford",
            brand = "Ford",
            images = setOf(EmptyImage),
            quantity = 2,
            isFavorite = true,
            description = "A powerful and versatile pickup truck.",
            category = "Truck",
            imageUrl = EmptyImage,
        )
    }
    ExchangeCarSelection(carResults = carResults, onCarClick = {})
}

@Preview
@Composable
private fun CarSelectionHeaderPreview() {
    CarSelectionHeader(
        onHomeClick = {},
        headerCallbacks = HeaderCallbacks.default,
    )
}
