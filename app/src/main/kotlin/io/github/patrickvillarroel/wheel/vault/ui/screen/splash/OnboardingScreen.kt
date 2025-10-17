package io.github.patrickvillarroel.wheel.vault.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val pages = listOf(
        R.drawable.onboarding_1,
        R.drawable.onboarding_2,
        R.drawable.onboarding_3,
        R.drawable.onboarding_4,
        R.drawable.onboarding_5,
    )

    OnboardingContent(
        pages = pages,
        onFinish = {
            viewModel.updateOnboardingState()
            onFinish()
        },
        modifier = modifier,
    )
}

@Composable
private fun OnboardingContent(pages: List<Int>, onFinish: () -> Unit, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF1D1B20),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            // Imagen de fondo por página
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = pages[page]),
                        contentDescription = stringResource(R.string.onboarding_step, page + 1),
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier.fillMaxHeight(),
                    )
                }
            }

            // Botón Omitir arriba
            TextButton(onClick = onFinish, Modifier.padding(top = 5.dp, end = 14.dp).align(Alignment.TopEnd)) {
                Text(stringResource(R.string.skip), color = Color.White)
            }

            OnboardingControls(
                currentPage = pagerState.currentPage,
                lastIndex = pages.lastIndex,
                onPreviousClick = {
                    scope.launch {
                        if (pagerState.currentPage > 0) {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                },
                onNextClick = {
                    scope.launch {
                        if (pagerState.currentPage < pages.lastIndex) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onFinish()
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun BoxScope.OnboardingControls(
    currentPage: Int,
    lastIndex: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    // Botón anterior
    if (currentPage > 0) {
        TextButton(onClick = onPreviousClick, Modifier.padding(24.dp).align(Alignment.BottomStart)) {
            Text(stringResource(R.string.back_variant), color = Color.White)
        }
    }

    // Botón siguiente o empezar
    Button(
        onClick = onNextClick,
        modifier = Modifier.padding(24.dp).align(Alignment.BottomEnd),
        colors = ButtonDefaults.buttonColors(Color(0xFFE42E31)),
    ) {
        Text(
            if (currentPage == lastIndex) stringResource(R.string.start) else stringResource(R.string.next),
            color = Color.White,
        )
    }
}

@PreviewScreenSizes
@PreviewLightDark
@Composable
private fun OnboardingPreview() {
    WheelVaultTheme {
        OnboardingContent(
            listOf(
                R.drawable.onboarding_1,
                R.drawable.onboarding_2,
                R.drawable.onboarding_3,
                R.drawable.onboarding_4,
                R.drawable.onboarding_5,
            ),
            onFinish = {},
        )
    }
}
