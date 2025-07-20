package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel

@Composable
fun DefaultFilterBar(onSearchClick: () -> Unit, callbacks: GarageCallbacks.FilterBar, modifier: Modifier = Modifier) {
    var selectedRecientes by rememberSaveable { mutableStateOf(false) }
    var selectedBrand by rememberSaveable { mutableStateOf("") }
    var selectedUltimos by rememberSaveable { mutableStateOf(false) }
    var selectedFav by rememberSaveable { mutableStateOf(false) }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier.padding(bottom = 15.dp, top = 8.dp),
    ) {
        item {
            AssistChip(
                onClick = callbacks.onHomeClick,
                label = { Icon(Icons.Filled.Home, stringResource(R.string.home), tint = Color.White) },
                colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF1D1B20)),
                border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = Color(0xFF1D1B20)),
            )
        }

        item {
            AssistChip(
                onClick = onSearchClick,
                label = { Text(stringResource(R.string.search)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, stringResource(R.string.search), tint = Color.White)
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFF1D1B20),
                    labelColor = Color.White,
                ),
                border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = Color(0xFF1D1B20)),
            )
        }

        item {
            FilterChip(
                selected = selectedFav,
                onClick = { callbacks.onFilterByFavorite(true) },
                label = { Text("Favoritos") },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFF1D1B20),
                    labelColor = Color.White,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selectedFav,
                    borderColor = Color(0xFF1D1B20),
                ),
            )
        }

        item {
            FilterChip(
                selected = selectedRecientes,
                onClick = { callbacks.onSortByRecent() },
                label = { Text(stringResource(R.string.recent)) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFF1D1B20),
                    labelColor = Color.White,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selectedRecientes,
                    borderColor = Color(0xFF1D1B20),
                ),
            )
        }

        item {
            FilterChip(
                selected = selectedUltimos,
                onClick = { callbacks.onSortByLast() },
                label = { Text(stringResource(R.string.last)) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFF1D1B20),
                    labelColor = Color.White,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selectedUltimos,
                    borderColor = Color(0xFF1D1B20),
                ),
            )
        }

        items(BrandViewModel.manufacturerList) { brand ->
            val isSelected = selectedBrand == brand
            FilterChip(
                selected = isSelected,
                onClick = { callbacks.onFilterByBrand(brand) },
                label = { Text(brand) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFF1D1B20),
                    labelColor = Color.White,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    isSelected,
                    borderColor = Color(0xFF1D1B20),
                ),
            )
        }
    }
}
