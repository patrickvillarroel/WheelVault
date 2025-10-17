package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun RaceDivider(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(top = 2.dp, bottom = 2.dp)) {
        // Línea sólida superior
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.LightGray,
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Línea discontinua (imitando rayas de carretera)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            repeat(20) {
                // Puedes ajustar la cantidad de rayas según el ancho
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(2.dp)
                        .background(Color.LightGray),
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Línea sólida inferior
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.LightGray,
        )
    }
}

@PreviewLightDark
@Composable
private fun RaceDividerPreview() {
    WheelVaultTheme {
        Surface {
            Column(Modifier.padding(25.dp)) {
                RaceDivider()
                Text("Hello World")
                RaceDivider()
            }
        }
    }
}
