package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BrandCard
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.CarCard
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun CollectorsHomeScreen(onAddClick: () -> Unit, onSearchClick: () -> Unit, modifier: Modifier = Modifier) {
    val closeMenu = stringResource(R.string.close) + stringResource(R.string.menu)
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    BackHandler(fabMenuExpanded) { fabMenuExpanded = false }
    val items = remember {
        listOf(
            Triple(Icons.Filled.Add, R.string.add, onAddClick),
            Triple(Icons.Filled.Search, R.string.search, onSearchClick),
        )
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButtonMenu(expanded = fabMenuExpanded, button = {
                ToggleFloatingActionButton(
                    checked = fabMenuExpanded,
                    onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
                    containerColor = { Color(0xFFE53935) },
                ) {
                    val imageVector by remember {
                        derivedStateOf {
                            if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                        }
                    }
                    Icon(
                        imageVector,
                        contentDescription = stringResource(R.string.search),
                        modifier = Modifier.animateIcon({ checkedProgress }),
                        tint = Color.White,
                    )
                }
            }) {
                items.forEachIndexed { i, (image, description, onClick) ->
                    FloatingActionButtonMenuItem(
                        onClick = {
                            fabMenuExpanded = false
                            onClick()
                        },
                        icon = { Icon(image, stringResource(description)) },
                        text = { Text(stringResource(description)) },
                        modifier = Modifier.semantics {
                            isTraversalGroup = true
                            if (i == items.size - 1) {
                                customActions = listOf(
                                    CustomAccessibilityAction(
                                        label = closeMenu,
                                        action = {
                                            fabMenuExpanded = false
                                            true
                                        },
                                    ),
                                )
                            }
                        },
                    )
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            item { TopHeader(onProfileClick = {}) }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Sección de marcas
            item {
                Text(
                    text = stringResource(R.string.brands),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(5) {
                        BrandCard(logo = R.drawable.hot_wheels_logo_black)
                    }
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(2.dp),
                    thickness = DividerDefaults.Thickness,
                    color = Color.Gray.copy(alpha = 0.3f),
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Sección de recientes
            item {
                Text(
                    text = stringResource(R.string.recently_added),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(5) {
                        CarCard(imageRes = R.drawable.batman_car)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Text(
                    text = stringResource(R.string.recently_added),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeContentPreview() {
    WheelVaultTheme {
        CollectorsHomeScreen({}, {})
    }
}
