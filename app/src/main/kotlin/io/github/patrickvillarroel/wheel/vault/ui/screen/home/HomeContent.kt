package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.VideoNews
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BrandCard
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.CarCard
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.RaceDivider
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.VideoCardPreview
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme
import io.github.patrickvillarroel.wheel.vault.ui.util.items
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

/**
 * @param brands where the first element is the id and the second is the image
 * @param recentCars where the first element is the id and the second is the image
 */
@Composable
fun HomeContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    brands: LazyPagingItems<Pair<Uuid, Any>>,
    news: LazyPagingItems<VideoNews>,
    recentCars: LazyPagingItems<Pair<Uuid, Any>>,
    info: HomeCallbacks,
    modifier: Modifier = Modifier,
) {
    val layoutDirection = LocalLayoutDirection.current
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        floatingActionButton = { HomeFloatingButton(onAddClick = info.onAddClick, onSearchClick = info.onSearchClick) },
    ) { paddingValues ->
        with(sharedTransitionScope) {
            PullToRefreshBox(
                isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    info.onRefresh()
                    coroutineScope.launch {
                        delay(800)
                        isRefreshing = false
                    }
                },
                Modifier.fillMaxSize().padding(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                    bottom = paddingValues.calculateBottomPadding(),
                ),
            ) {
                LazyColumn(Modifier.fillMaxSize()) {
                    // mini TODO header have inside padding of top bar
                    item { HomeCarHeader(info, Modifier.fillMaxWidth()) }
                    // Sección de marcas
                    item {
                        Text(
                            text = stringResource(R.string.brands),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    }

                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(brands, key = { (id, _) -> id }) { (id, image) ->
                                BrandCard(
                                    logo = image,
                                    onClick = { info.onBrandClick(id) },
                                    modifier = Modifier.sharedBounds(
                                        rememberSharedContentState("brand-$id"),
                                        animatedVisibilityScope,
                                    ),
                                )
                            }
                        }
                    }

                    item { RaceDivider() }

                    // Sección de recientes
                    item {
                        Text(
                            text = stringResource(R.string.recently_added),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    }

                    item {
                        if (recentCars.itemCount == 0) {
                            Image(
                                painterResource(R.drawable.baner_add_car),
                                stringResource(R.string.add_car),
                                Modifier.fillMaxWidth().clickable(onClick = info.onAddClick),
                            )
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(recentCars, key = { (id, _) -> id }) { (id, image) ->
                                CarCard(
                                    image = image,
                                    onClick = { info.onCarClick(id) },
                                    modifier = Modifier.sharedBounds(
                                        rememberSharedContentState("car-$id"),
                                        animatedVisibilityScope,
                                    ),
                                )
                            }
                        }
                    }

                    item { RaceDivider() }

                    item {
                        Text(
                            text = stringResource(R.string.information_interest),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    }

                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(news, key = { it.id }) { video ->
                                VideoCardPreview(video.thumbnail, video.name, onPlayClick = { info.onNewsClick(video) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@PreviewScreenSizes
@PreviewLightDark
@Composable
private fun HomeContentPreview() {
    val pagingData = PagingData.from(
        data = List(10) {
            VideoNews(
                thumbnail = R.drawable.thumbnail_example,
                id = Uuid.random(),
                name = "Example",
                link = "Example",
                description = "A video of hot wheels events.",
                createdAt = null,
            )
        },
        sourceLoadStates = LoadStates(
            refresh = LoadState.NotLoading(endOfPaginationReached = false),
            prepend = LoadState.NotLoading(endOfPaginationReached = true),
            append = LoadState.NotLoading(endOfPaginationReached = false),
        ),
    )
    val fakeFlow = MutableStateFlow(pagingData)
    val newsPaged = fakeFlow.collectAsLazyPagingItems()

    val pagingDataBrands = PagingData.from<Pair<Uuid, Any>>(
        List(10) { Uuid.random() to R.drawable.hot_wheels_logo_black },
        sourceLoadStates = LoadStates(
            refresh = LoadState.NotLoading(endOfPaginationReached = false),
            prepend = LoadState.NotLoading(endOfPaginationReached = true),
            append = LoadState.NotLoading(endOfPaginationReached = false),
        ),
    )
    val fakeFlowBrands = MutableStateFlow(pagingDataBrands)
    val brandsPaged = fakeFlowBrands.collectAsLazyPagingItems()

    val pagingDataCars = PagingData.from<Pair<Uuid, Any>>(
        List(10) { Uuid.random() to R.drawable.batman_car },
        sourceLoadStates = LoadStates(
            refresh = LoadState.NotLoading(endOfPaginationReached = false),
            prepend = LoadState.NotLoading(endOfPaginationReached = true),
            append = LoadState.NotLoading(endOfPaginationReached = false),
        ),
    )
    val fakeFlowCars = MutableStateFlow(pagingDataCars)
    val carsPaged = fakeFlowCars.collectAsLazyPagingItems()

    SharedTransitionLayout {
        AnimatedVisibility(true) {
            WheelVaultTheme {
                HomeContent(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    brands = brandsPaged,
                    news = newsPaged,
                    recentCars = carsPaged,
                    info = HomeCallbacks.default,
                )
            }
        }
    }
}

@PreviewScreenSizes
@PreviewLightDark
@Composable
private fun HomeNoContentPreview() {
    val pagingData = PagingData.empty<VideoNews>(
        LoadStates(
            refresh = LoadState.NotLoading(endOfPaginationReached = false),
            prepend = LoadState.NotLoading(endOfPaginationReached = true),
            append = LoadState.NotLoading(endOfPaginationReached = false),
        ),
    )
    val fakeFlow = MutableStateFlow(pagingData)
    val newsPaged = fakeFlow.collectAsLazyPagingItems()

    val pagingDataBrands = PagingData.empty<Pair<Uuid, Any>>(
        LoadStates(
            refresh = LoadState.NotLoading(endOfPaginationReached = false),
            prepend = LoadState.NotLoading(endOfPaginationReached = true),
            append = LoadState.NotLoading(endOfPaginationReached = false),
        ),
    )
    val fakeFlowBrands = MutableStateFlow(pagingDataBrands)
    val brandsPaged = fakeFlowBrands.collectAsLazyPagingItems()

    val pagingDataCars = PagingData.empty<Pair<Uuid, Any>>(
        LoadStates(
            refresh = LoadState.NotLoading(endOfPaginationReached = false),
            prepend = LoadState.NotLoading(endOfPaginationReached = true),
            append = LoadState.NotLoading(endOfPaginationReached = false),
        ),
    )
    val fakeFlowCars = MutableStateFlow(pagingDataCars)
    val carsPaged = fakeFlowCars.collectAsLazyPagingItems()

    SharedTransitionLayout {
        AnimatedVisibility(true) {
            WheelVaultTheme {
                HomeContent(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    brands = brandsPaged,
                    news = newsPaged,
                    recentCars = carsPaged,
                    info = HomeCallbacks.default,
                )
            }
        }
    }
}
