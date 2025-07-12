package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp

@Composable
fun DefaultFilterBar(onSearchClick: () -> Unit, onHomeClick: () -> Unit, modifier: Modifier = Modifier) {
    var selectedRecientes by rememberSaveable { mutableStateOf(false) }
    var selectedBrand by rememberSaveable { mutableStateOf(false) }
    var selectedModel by rememberSaveable { mutableStateOf(false) }

    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(bottom = 8.dp),
    ) {
        item {
            AssistChip(
                onClick = onHomeClick,
                label = { Icon(Icons.Filled.Home, "Home", tint = Color.White) },
                colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF1D1B20)),
                border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = Color(0xFF1D1B20)),
            )
        }
        item {
            AssistChip(
                onClick = onSearchClick,
                label = { Text("Buscar") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
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
                selected = selectedRecientes,
                onClick = { /* filter */ },
                label = { Text("Recientes") },
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
                selected = selectedBrand,
                onClick = { /* filter */ },
                label = { Text("HotWheels") },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFF1D1B20),
                    labelColor = Color.White,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selectedBrand,
                    borderColor = Color(0xFF1D1B20),
                ),
            )
        }
        item {
            FilterChip(
                selected = selectedModel,
                onClick = { /* filter */ },
                label = { Text("MiniGT") },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFF1D1B20),
                    labelColor = Color.White,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selectedModel,
                    borderColor = Color(0xFF1D1B20),
                ),
            )
        }
    }
}
