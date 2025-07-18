package io.github.patrickvillarroel.wheel.vault.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onFinish: () -> Unit, modifier: Modifier = Modifier) {
    val pages = listOf(
        R.drawable.onboarding_1,
        R.drawable.onboarding_2,
        R.drawable.onboarding_3,
        R.drawable.onboarding_4,
        R.drawable.onboarding_5,
    )

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
                modifier = Modifier.fillMaxSize().align(Alignment.Center),
            ) { page ->
                Image(
                    painter = painterResource(id = pages[page]),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.aspectRatio(9f / 16f).fillMaxHeight().align(Alignment.Center),
                )
            }

            // Botón Omitir reposicionado (no tan arriba)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, end = 14.dp),
                contentAlignment = Alignment.TopEnd,
            ) {
                TextButton(onClick = onFinish) {
                    Text("Omitir", color = Color.White)
                }
            }

            // Controles abajo
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Botón anterior
                    if (pagerState.currentPage > 0) {
                        TextButton(onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }) {
                            Text("Anterior", color = Color.White)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(80.dp)) // espacio fantasma
                    }

                    // Botón siguiente o empezar
                    OutlinedButton(onClick = {
                        scope.launch {
                            if (pagerState.currentPage < pages.lastIndex) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                onFinish()
                            }
                        }
                    }) {
                        Text(
                            if (pagerState.currentPage == pages.lastIndex) "Empezar" else "Siguiente",
                        )
                    }
                }
            }
        }
    }
}

@PreviewScreenSizes
@PreviewLightDark
@Composable
private fun OnboardingPreview() {
    WheelVaultTheme {
        OnboardingScreen(onFinish = {})
    }
}
