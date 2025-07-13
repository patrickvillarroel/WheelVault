package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem

@Composable
fun BrandDetailScreen(
    brandId: Int,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onGarageClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onAddClick: () -> Unit,
    onCarClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO simulate brand search with VM
    val brands = remember {
        listOf(
            Triple(
                R.drawable.hot_wheels_logo_black to "Hot Wheels",
                "En 1968, los coches de metal de Hot Wheels se diseñaron para revolucionar el mundo de los coches de juguete con el objetivo de ofrecer un diseño más detallado y un mejor rendimiento que los de la competencia. Cinco décadas más tarde, Hot Wheels es número 1 en ventas de juguetes en el mundo.\nHot Wheels se ha convertido en un referente tanto de la cultura automovilística como de la popular gracias a los eventos en directo, como el HW Legends Tour, a los eventos deportivos HW Superchargers y a las atracciones de los parques temáticos, así como a sus colaboraciones con algunas de las marcas más conocidas.",
                List(10) {
                    CarItem(
                        model = "Ford Mustang GTD",
                        year = 2025,
                        manufacturer = "HotWheels",
                        quantity = 2,
                        imageUrl = "https://m.media-amazon.com/images/I/61iE8unK0XL._AC_SL1069_.jpg",
                        isFavorite = true,
                    ) to { _: Boolean -> }
                },
            ),
        )
    }
    val (iconDetail, description, cars) = remember(brandId) { brands.getOrNull(brandId) ?: brands.first() }

    BrandDetailContent(
        iconDetail.second,
        iconDetail,
        description = description,
        carCollection = cars,
        onBackClick = onBackClick,
        onProfileClick = onProfileClick,
        onGarageClick = onGarageClick,
        onFavoritesClick = onFavoritesClick,
        onStatisticsClick = onStatisticsClick,
        onAddClick = onAddClick,
        onCarClick = { onCarClick(it.id) },
        modifier = modifier,
    )
}
