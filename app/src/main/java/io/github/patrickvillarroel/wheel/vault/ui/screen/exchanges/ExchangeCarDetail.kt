package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BackTextButton
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeroImageCarousel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuHeader
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarDetail
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarDetailCallbacks

@Composable
fun ExchangeCarDetailContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    carDetail: CarItem,
    requestText: String,
    callbacks: CarDetailCallbacks,
    onExchangeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MenuHeader(callbacks.headersBackCallbacks) {
                BackTextButton(onBack = callbacks.headersBackCallbacks.onBackClick)
            }
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                FilledIconButton(
                    onExchangeClick,
                    modifier = Modifier.size(65.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFE42E31)),
                ) {
                    Icon(Icons.Filled.ChangeCircle, stringResource(R.string.exchange), Modifier.size(40.dp))
                }
                Text(
                    stringResource(R.string.exchange_action),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        with(sharedTransitionScope) {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
                            stringResource(R.string.information_of_car),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(top = 16.dp),
                        )
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Filled.Share, stringResource(R.string.share))
                        }
                    }
                }

                CarDetail(carDetail)

                item {
                    Text(
                        stringResource(R.string.request),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )

                    Box(
                        Modifier
                            .border(width = 1.dp, color = Color(0xFFE42E31), shape = RoundedCornerShape(size = 15.dp))
                            .width(388.dp)
                            .height(86.dp)
                            .background(color = Color(0xFF2C2930), shape = RoundedCornerShape(size = 15.dp)),
                    ) {
                        Text(
                            requestText,
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                item {
                    Box(Modifier.height(100.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ExchangeCarDetailContentPreview() {
    val carDetail = CarItem(
        model = "Camry",
        year = 2022,
        manufacturer = "Toyota",
        brand = "Toyota",
        images = setOf(CarItem.EmptyImage),
    )
    val callbacks = CarDetailCallbacks.default(carDetail)

    SharedTransitionScope {
        AnimatedVisibility(true) {
            ExchangeCarDetailContent(
                sharedTransitionScope = this@SharedTransitionScope,
                animatedVisibilityScope = this,
                carDetail = carDetail,
                requestText = "Busco porsche rallyE color verde, si es STH mejor.",
                callbacks = callbacks,
                onExchangeClick = {},
                modifier = it,
            )
        }
    }
}
