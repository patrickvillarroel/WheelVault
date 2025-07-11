package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BrandHeader
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun BrandDetailContent(
    brandIconDetail: Pair<Int, String?>,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier, topBar = {
        BrandHeader(
            icon = brandIconDetail,
            onBackClick = onBackClick,
            onProfileClick = onProfileClick,
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        )
    }) { paddingValues ->
        LazyColumn(Modifier.padding(paddingValues).padding(15.dp)) {
            item { Text("Info", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun BrandPreview() {
    WheelVaultTheme {
        BrandDetailContent(
            R.drawable.hot_wheels_logo_black to "Hot Wheels Logo",
            onBackClick = {},
            onProfileClick = {},
        )
    }
}
