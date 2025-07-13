package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem

@Composable
fun CarDetailScreen(
    carId: Int,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onGarageClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO replace with VM
    val cars = remember {
        List(10) {
            CarItem(
                model = "Ford Mustang GTD",
                year = 2025,
                manufacturer = "HotWheels",
                quantity = 2,
                images = listOf(
                    "https://m.media-amazon.com/images/I/61iE8unK0XL._AC_SL1069_.jpg",
                    "https://m.media-amazon.com/images/I/61ojzr1uMCL.jpg",
                    "https://th.bing.com/th/id/R.6c22f47603c5163100d66383df8468fb?rik=J6k0pKwJXl45KQ&pid=ImgRaw&r=0",
                ),
                isFavorite = true,
                brand = "Hot Wheels",
            )
        }
    }

    val carDetail = remember(carId) { cars.getOrNull(carId) ?: cars.first() }

    CarDetailContent(
        carDetail = carDetail,
        onBack = onBackClick,
        onProfileClick = onProfileClick,
        onGarageClick = onGarageClick,
        onFavoritesClick = onFavoritesClick,
        onStatisticsClick = onStatisticsClick,
        onEditClick = {},
        onDeleteClick = {},
        onFavoriteToggle = {},
        modifier = modifier,
    )
}
