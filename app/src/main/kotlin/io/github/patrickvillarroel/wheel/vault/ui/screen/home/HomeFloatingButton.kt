package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
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
import io.github.patrickvillarroel.wheel.vault.R

@Composable
fun HomeFloatingButton(onAddClick: () -> Unit, onSearchClick: () -> Unit, modifier: Modifier = Modifier) {
    val closeMenu = stringResource(R.string.close) + stringResource(R.string.menu)
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val items = remember {
        listOf(
            Triple(Icons.Filled.Add, R.string.add, onAddClick),
            Triple(Icons.Filled.Search, R.string.search, onSearchClick),
        )
    }

    BackHandler(fabMenuExpanded) { fabMenuExpanded = false }
    FloatingActionButtonMenu(
        expanded = fabMenuExpanded,
        modifier = modifier,
        button = {
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
        },
    ) {
        items.forEachIndexed { i, (image, description, onClick) ->
            FloatingActionButtonMenuItem(
                onClick = {
                    fabMenuExpanded = false
                    onClick.invoke()
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
}
