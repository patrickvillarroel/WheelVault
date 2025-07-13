package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem

@Composable
fun GarageScreen(
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit,
    onAddClick: () -> Unit,
    onCarClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO add VM replace this fake
    // TODO receive filters and apply it
    val result = remember {
        List(10) {
            CarItem(
                model = "Ford Mustang GTD",
                year = 2025,
                manufacturer = "HotWheels",
                quantity = 2,
                imageUrl =
                "https://tse1.mm.bing.net/th/id/OIP.zfsbW7lEIwYgeUt7Fd1knwHaHg?rs=1&pid=ImgDetMain&o=7&rm=3",
                isFavorite = true,
            )
        }
    }

    // TODO replace all clicks with VM
    GarageContent(
        result,
        onProfileClick = onProfileClick,
        onHomeClick = onHomeClick,
        onSearch = {},
        onAddClick = onAddClick,
        onCarClick = { onCarClick(it.id) },
        onToggleFavorite = { _, _ -> },
        onFavoritesClick = {},
        onStatisticsClick = {},
        modifier = modifier,
    )
}
