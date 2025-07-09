package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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

@Composable
fun MenuButtonHeader(
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit = {},
) {
    var expandedMenu by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        IconButton({ expandedMenu = !expandedMenu }) {
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
                onClick = { /* Do something... */ },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.favorites)) },
                leadingIcon = { Icon(Icons.Outlined.Favorite, contentDescription = null) },
                onClick = { /* Do something... */ },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.statistics)) },
                leadingIcon = { Icon(Icons.Outlined.Calculate, contentDescription = null) },
                onClick = { /* Do something... */ },
            )
        }
        content()
        IconButton(onProfileClick) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(R.string.profile),
                tint = Color.White,
            )
        }
    }
}

@Preview
@Composable
private fun HeaderBrushPreview() {
    WheelVaultTheme {
        MenuButtonHeader(onProfileClick = {})
    }
}
