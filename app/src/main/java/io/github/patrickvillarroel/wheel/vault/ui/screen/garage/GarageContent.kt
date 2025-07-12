package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.CarItemCard
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun GarageContent(
    carResults: List<CarItem>,
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit,
    onSearch: (String) -> Unit,
    onAddClick: () -> Unit,
    onToggleFavorite: (Int, Boolean) -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO move the state to parent composable or receive the query to make the query from other screens
    var uiState by rememberSaveable { mutableStateOf(GarageUiState.DEFAULT) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GarageTopBar(
                uiState,
                searchQuery = searchQuery,
                onSearchQueryChange = {
                    searchQuery = it
                    onSearch(it)
                },
                onStateChange = { uiState = it },
                onProfileClick = onProfileClick,
                onHomeClick = onHomeClick,
                onFavoriteClick = onFavoritesClick,
                onStaticsClick = onStatisticsClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick, containerColor = Color(0xFFE42E31)) {
                Icon(Icons.Filled.Add, stringResource(R.string.add), tint = Color.Black)
            }
        },
    ) { paddingValues ->
        LazyColumn(Modifier.padding(paddingValues).fillMaxSize().padding(start = 15.dp, end = 15.dp)) {
            item {
                Text(
                    stringResource(R.string.garage),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 15.dp, start = 15.dp, bottom = 5.dp),
                )
            }
            items(carResults, key = { it.id }) { item ->
                CarItemCard(item, onFavoriteToggle = { onToggleFavorite(item.id, it) }, Modifier.padding(3.dp))
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun GaragePreview() {
    WheelVaultTheme {
        GarageContent(
            List(10) {
                CarItem(
                    name = "Ford Mustang GTD",
                    year = 2025,
                    manufacturer = "HotWheels",
                    quantity = 2,
                    imageUrl =
                    "https://tse1.mm.bing.net/th/id/OIP.zfsbW7lEIwYgeUt7Fd1knwHaHg?rs=1&pid=ImgDetMain&o=7&rm=3",
                    isFavorite = true,
                )
            },
            onProfileClick = {},
            onHomeClick = {},
            onSearch = {},
            onAddClick = {},
            onToggleFavorite = { _, _ -> },
            onFavoritesClick = {},
            onStatisticsClick = {},
        )
    }
}
