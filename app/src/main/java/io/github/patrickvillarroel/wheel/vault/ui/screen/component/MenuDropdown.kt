package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Suppress("ktlint:compose:multiple-emitters-check")
@Composable
fun MenuDropDown(menuDropdownCallbacks: HeaderMenuDropdownCallbacks, modifier: Modifier = Modifier) {
    var expandedMenu by remember { mutableStateOf(false) }
    IconButton({ expandedMenu = !expandedMenu }, modifier = modifier) {
        AnimatedContent(expandedMenu, label = "Menu Swip") {
            Icon(
                imageVector = if (!it) Icons.Default.Menu else Icons.Default.ViewWeek,
                contentDescription = stringResource(R.string.menu),
                tint = Color.White,
            )
        }
    }
    DropdownMenu(
        expanded = expandedMenu,
        onDismissRequest = { expandedMenu = false },
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.garage)) },
            leadingIcon = { Icon(Icons.Outlined.DirectionsCar, contentDescription = null) },
            onClick = {
                expandedMenu = false
                menuDropdownCallbacks.onGarageClick()
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.favorites)) },
            leadingIcon = { Icon(Icons.Outlined.Favorite, contentDescription = null) },
            onClick = {
                expandedMenu = false
                menuDropdownCallbacks.onFavoritesClick()
            },
        )
    }
}

@Preview
@Composable
private fun HeaderBrushPreview() {
    WheelVaultTheme {
        Column {
            MenuDropDown(HeaderMenuDropdownCallbacks(onGarageClick = {}, onFavoritesClick = {}, onStatisticsClick = {}))
        }
    }
}
