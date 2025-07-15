package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderMenuDropdownCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuDropDown

@Composable
private fun BrandHeader(
    icon: Pair<Int, String?>,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onGarageClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    brandName: String = icon.second!!,
) {
    LargeFlexibleTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = brandName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        subtitle = {
            Image(
                painter = painterResource(icon.first),
                contentDescription = icon.second,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Fit,
            )
        },
        navigationIcon = {
            TextButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White,
                )
                Text(
                    text = stringResource(R.string.back),
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        actions = {
            MenuDropDown(
                HeaderMenuDropdownCallbacks(
                    onGarageClick = onGarageClick,
                    onFavoritesClick = onFavoritesClick,
                    onStatisticsClick = onStatisticsClick,
                ),
            )
            IconButton(onClick = onProfileClick) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = stringResource(R.string.profile),
                    tint = Color.White,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFE42E31),
            scrolledContainerColor = Color(0xFFD32F2F),
            navigationIconContentColor = Color.Black,
            titleContentColor = Color.Black,
            actionIconContentColor = Color.White,
        ),
    )
}

@Composable
private fun BrandHeader(
    icon: Pair<Int, String?>,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onGarageClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    LargeTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = {
            Box {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MenuDropDown(
                        HeaderMenuDropdownCallbacks(
                            onGarageClick = onGarageClick,
                            onFavoritesClick = onFavoritesClick,
                            onStatisticsClick = onStatisticsClick,
                        ),
                    )
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.profile),
                            tint = Color.White,
                        )
                    }
                }
                Image(
                    painter = painterResource(icon.first),
                    contentDescription = icon.second,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .height(160.dp),
                    contentScale = ContentScale.Fit,
                )
                TextButton(onClick = onBackClick, modifier = Modifier.align(Alignment.BottomStart)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.Black,
                    )
                    Text(
                        text = stringResource(R.string.back),
                        color = Color.Black,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFE42E31),
            scrolledContainerColor = Color(0xFFD32F2F),
            navigationIconContentColor = Color.Black,
            titleContentColor = Color.Black,
            actionIconContentColor = Color.White,
        ),
    )
}
