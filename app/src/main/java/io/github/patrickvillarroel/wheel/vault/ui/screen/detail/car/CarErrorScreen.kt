package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel

@Composable
fun CarErrorScreen(state: CarViewModel.CarDetailUiState, modifier: Modifier = Modifier) {
    Scaffold(modifier.fillMaxSize()) {
        Column(
            Modifier.padding(it).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painterResource(
                    if (state is CarViewModel.CarDetailUiState.Error) {
                        R.drawable.error
                    } else {
                        R.drawable.no_data
                    },
                ),
                null,
                Modifier.padding(16.dp).fillMaxWidth(0.8f),
            )
            Text(
                if (state is CarViewModel.CarDetailUiState.Error) {
                    stringResource(R.string.error_loading_of, stringResource(R.string.car))
                } else {
                    stringResource(R.string.car_not_found)
                },
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
