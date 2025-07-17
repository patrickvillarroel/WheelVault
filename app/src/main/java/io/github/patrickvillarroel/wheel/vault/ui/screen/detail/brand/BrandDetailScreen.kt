package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import java.util.UUID

@Composable
fun BrandDetailScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    brandId: UUID,
    onAddClick: () -> Unit,
    onCarClick: (UUID) -> Unit,
    headerBackCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
) {
    // TODO simulate brand search with VM
    val brands = remember {
        listOf(
            Brand(
                name = "Hot Wheels",
                description =
                "En 1968, los coches de metal de Hot Wheels se diseñaron para revolucionar el mundo de los coches de juguete con el objetivo de ofrecer un diseño más detallado y un mejor rendimiento que los de la competencia. Cinco décadas más tarde, Hot Wheels es número 1 en ventas de juguetes en el mundo.\nHot Wheels se ha convertido en un referente tanto de la cultura automovilística como de la popular gracias a los eventos en directo, como el HW Legends Tour, a los eventos deportivos HW Superchargers y a las atracciones de los parques temáticos, así como a sus colaboraciones con algunas de las marcas más conocidas.",
                image = R.drawable.hot_wheels_logo_black,
                contentDescription = "Hot Wheels Logo",
            ) to List(10) {
                CarItem(
                    model = "Ford Mustang GTD",
                    year = 2025,
                    manufacturer = "HotWheels",
                    quantity = 2,
                    imageUrl = "https://m.media-amazon.com/images/I/61iE8unK0XL._AC_SL1069_.jpg",
                    isFavorite = true,
                )
            },
        )
    }
    val (brand, cars) = remember(brandId) { brands.firstOrNull { it.first.id == brandId } ?: brands.first() }

    BrandDetailContent(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        brandDetail = BrandDetail(
            brand = brand,
            carCollection = cars,
            onAddClick = onAddClick,
            onCarClick = onCarClick,
            onFavoriteToggle = { _, _ -> },
            headerBackCallbacks = headerBackCallbacks,
            animationKey = "brand-$brandId",
        ),
        modifier = modifier,
    )
}
