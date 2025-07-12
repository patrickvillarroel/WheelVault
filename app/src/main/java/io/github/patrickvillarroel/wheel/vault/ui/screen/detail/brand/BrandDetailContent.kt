package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BrandHeader
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.CarItemCard
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

// TODO move parameters into data class, carCollection is not immutable
@Composable
fun BrandDetailContent(
    brandName: String,
    brandIconDetail: Pair<Int, String>,
    description: String,
    carCollection: List<Pair<CarItem, (Boolean) -> Unit>>,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onGarageClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BrandHeader(
                icon = brandIconDetail,
                onBackClick = onBackClick,
                onProfileClick = onProfileClick,
                onGarageClick = onGarageClick,
                onFavoritesClick = onFavoritesClick,
                onStatisticsClick = onStatisticsClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick, containerColor = Color(0xFFE42E31)) {
                Icon(Icons.Filled.Add, stringResource(R.string.add), tint = Color.Black)
            }
        },
    ) { paddingValues ->
        LazyColumn(
            Modifier.fillMaxSize().padding(top = 15.dp, start = 15.dp, end = 15.dp),
            contentPadding = paddingValues,
        ) {
            item {
                Text(
                    "Info $brandName",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            item {
                Text(
                    description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify,
                )
            }
            item {
                Text(
                    "Carritos en la colección:",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 5.dp),
                )
            }
            items(carCollection) { (car, onFavorite) ->
                CarItemCard(car, onFavoriteToggle = onFavorite, Modifier.padding(bottom = 5.dp))
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun BrandPreview() {
    WheelVaultTheme {
        BrandDetailContent(
            "Hot Wheels",
            R.drawable.hot_wheels_logo_black to "Hot Wheels Logo",
            description =
            "En 1968, los coches de metal de Hot Wheels se diseñaron para revolucionar el mundo de los coches de juguete con el objetivo de ofrecer un diseño más detallado y un mejor rendimiento que los de la competencia. Cinco décadas más tarde, Hot Wheels es número 1 en ventas de juguetes en el mundo.\nHot Wheels se ha convertido en un referente tanto de la cultura automovilística como de la popular gracias a los eventos en directo, como el HW Legends Tour, a los eventos deportivos HW Superchargers y a las atracciones de los parques temáticos, así como a sus colaboraciones con algunas de las marcas más conocidas.",
            carCollection = listOf(
                CarItem(
                    name = "Ford Mustang GTD",
                    year = 2025,
                    manufacturer = "HotWheels",
                    quantity = 2,
                    imageUrl =
                    "https://tse1.mm.bing.net/th/id/OIP.zfsbW7lEIwYgeUt7Fd1knwHaHg?rs=1&pid=ImgDetMain&o=7&rm=3",
                    isFavorite = true,
                ) to {},
            ),
            onBackClick = {},
            onProfileClick = {},
            onGarageClick = {},
            onFavoritesClick = {},
            onAddClick = {},
            onStatisticsClick = {},
        )
    }
}
