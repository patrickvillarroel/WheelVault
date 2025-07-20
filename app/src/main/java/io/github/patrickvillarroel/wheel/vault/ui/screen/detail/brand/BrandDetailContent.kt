package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
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
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.CarItemCard
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun BrandDetailContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    brandDetail: BrandDetail,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                BrandHeader(
                    logoAndDescription = brandDetail.iconDetail,
                    headerBackCallbacks = brandDetail.headerBackCallbacks,
                    Modifier.sharedBounds(
                        rememberSharedContentState(brandDetail.animationKey),
                        animatedVisibilityScope,
                    ),
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = brandDetail.onAddClick, containerColor = Color(0xFFE42E31)) {
                    Icon(Icons.Filled.Add, stringResource(R.string.add), tint = Color.Black)
                }
            },
        ) { paddingValues ->
            LazyColumn(Modifier.padding(paddingValues).fillMaxSize().padding(top = 15.dp, start = 15.dp, end = 15.dp)) {
                item {
                    Text(
                        stringResource(R.string.info_of, brandDetail.brand.name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                item {
                    Text(
                        brandDetail.brand.description,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Justify,
                    )
                }

                item {
                    Text(
                        stringResource(R.string.car_in_collection),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }

                items(brandDetail.carCollection) { car ->
                    CarItemCard(
                        carItem = car,
                        onClick = { brandDetail.onCarClick(car.id) },
                        onFavoriteToggle = { brandDetail.onFavoriteToggle(car, it) },
                        modifier = Modifier
                            .padding(bottom = 7.dp)
                            .sharedBounds(rememberSharedContentState("car-${car.id}"), animatedVisibilityScope),
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun BrandPreview() {
    WheelVaultTheme {
        SharedTransitionLayout {
            AnimatedVisibility(true) {
                BrandDetailContent(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    brandDetail = BrandDetail(
                        brandInfo = BrandInfo(
                            brand = Brand(
                                name = "Hot Wheels",
                                description =
                                "En 1968, los coches de metal de Hot Wheels se diseñaron para revolucionar el mundo de los coches de juguete con el objetivo de ofrecer un diseño más detallado y un mejor rendimiento que los de la competencia. Cinco décadas más tarde, Hot Wheels es número 1 en ventas de juguetes en el mundo.\nHot Wheels se ha convertido en un referente tanto de la cultura automovilística como de la popular gracias a los eventos en directo, como el HW Legends Tour, a los eventos deportivos HW Superchargers y a las atracciones de los parques temáticos, así como a sus colaboraciones con algunas de las marcas más conocidas.",
                                image = R.drawable.hot_wheels_logo_black,
                                contentDescription = "Hot Wheels Logo",
                            ),
                            carCollection = listOf(
                                CarItem(
                                    model = "Ford Mustang GTD",
                                    year = 2025,
                                    manufacturer = "HotWheels",
                                    quantity = 2,
                                    imageUrl =
                                    "https://tse1.mm.bing.net/th/id/OIP.zfsbW7lEIwYgeUt7Fd1knwHaHg?rs=1&pid=ImgDetMain&o=7&rm=3",
                                    isFavorite = true,
                                ),
                            ),
                        ),
                        onAddClick = {},
                        onCarClick = {},
                        onFavoriteToggle = { _, _ -> },
                        headerBackCallbacks = HeaderBackCallbacks(
                            onBackClick = {},
                            onProfileClick = {},
                            onGarageClick = {},
                            onFavoritesClick = {},
                            onStatisticsClick = {},
                        ),
                    ),
                )
            }
        }
    }
}
