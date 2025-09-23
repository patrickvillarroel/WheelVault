package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun MenuButtonHeader(
    headerCallbacks: HeaderCallbacks,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        title = title,
        navigationIcon = { MenuDropDown(headerCallbacks) },
        actions = {
            IconButton(onClick = headerCallbacks.onProfileClick) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = stringResource(R.string.profile),
                    tint = Color.White,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
    )
}

@Preview
@Composable
private fun HeaderBrushPreview() {
    WheelVaultTheme {
        Column {
            MenuButtonHeader(HeaderCallbacks.default)
        }
    }
}
