package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BackTextButton
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.FavoriteIcon
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeroImageCarousel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuHeader
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.RaceDivider
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun CarDetailContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    callbacks: CarDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val carDetail = callbacks.carDetail

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MenuHeader(callbacks.headersBackCallbacks) {
                BackTextButton(onBack = callbacks.headersBackCallbacks.onBackClick)
            }
        },
    ) { paddingValues ->
        with(sharedTransitionScope) {
            PullToRefreshBox(
                isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    callbacks.onRefresh()
                    isRefreshing = false
                },
                Modifier.fillMaxSize().padding(paddingValues),
            ) {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    item {
                        HeroImageCarousel(
                            carDetail.images,
                            Modifier.sharedBounds(
                                rememberSharedContentState("car-${carDetail.id}"),
                                animatedVisibilityScope,
                            ),
                        )
                    }

                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "Información de Carrito",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(top = 16.dp),
                            )
                            FavoriteIcon(carDetail.isFavorite, callbacks.onFavoriteToggle)
                        }
                    }

                    item {
                        Text("Marca", style = MaterialTheme.typography.labelLarge)
                        Text(carDetail.brand, style = MaterialTheme.typography.bodyLarge)
                        RaceDivider()
                    }

                    item {
                        Text("Modelo", style = MaterialTheme.typography.labelLarge)
                        Text(carDetail.model, style = MaterialTheme.typography.bodyLarge)
                        RaceDivider()
                    }

                    item {
                        Text("Año", style = MaterialTheme.typography.labelLarge)
                        Text(carDetail.year.toString(), style = MaterialTheme.typography.bodyLarge)
                        RaceDivider()
                    }

                    item {
                        Text("Fabricante", style = MaterialTheme.typography.labelLarge)
                        Text(carDetail.manufacturer, style = MaterialTheme.typography.bodyLarge)
                        RaceDivider()
                    }

                    item {
                        Text("Categoría", style = MaterialTheme.typography.labelLarge)
                        Text(carDetail.category ?: "--", style = MaterialTheme.typography.bodyLarge)
                        RaceDivider()
                    }

                    item {
                        Text("Descripción", style = MaterialTheme.typography.labelLarge)
                        Text(carDetail.description ?: "--", style = MaterialTheme.typography.bodyLarge)
                        RaceDivider()
                    }

                    item {
                        Text(
                            text = "Cantidad: ${carDetail.quantity}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                        )
                        RaceDivider()
                    }

                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally),
                            ButtonGroupDefaults.HorizontalArrangement,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                OutlinedIconButton(
                                    onClick = callbacks.onEditClick,
                                    modifier = Modifier.size(65.dp),
                                ) {
                                    Icon(Icons.Filled.Edit, "Editar", Modifier.size(32.dp))
                                }
                                Text("Editar", fontWeight = FontWeight.SemiBold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                OutlinedIconButton(
                                    onClick = callbacks.onDeleteClick,
                                    modifier = Modifier.size(65.dp),
                                ) {
                                    Icon(Icons.Outlined.Delete, "Eliminar", Modifier.size(32.dp), Color(0xFFE42E31))
                                }
                                Text("Eliminar", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CarDetailContentPreview() {
    val carDetail = remember {
        CarItem(
            brand = "Toyota",
            model = "Corolla",
            year = 2022,
            manufacturer = "Japan",
            description = "A reliable and fuel-efficient sedan.",
            category = "Sedan",
            quantity = 5,
            images = setOf("image1.jpg", "image2.jpg", "image3.jpg"),
        )
    }

    WheelVaultTheme {
        SharedTransitionLayout {
            AnimatedVisibility(true) {
                CarDetailContent(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    callbacks = CarDetailCallbacks(
                        carDetail = carDetail,
                        headersBackCallbacks = HeaderBackCallbacks(
                            onBackClick = {},
                            onProfileClick = {},
                            onGarageClick = {},
                            onFavoritesClick = {},
                            onStatisticsClick = {},
                        ),
                        onEditClick = {},
                        onDeleteClick = {},
                        onFavoriteToggle = {},
                        onRefresh = {},
                    ),
                )
            }
        }
    }
}
