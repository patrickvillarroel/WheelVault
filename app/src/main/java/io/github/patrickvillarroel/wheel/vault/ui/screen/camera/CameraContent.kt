package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun CameraLensContent(
    recognizedText: String,
    isProcessing: Boolean,
    showControls: Boolean,
    isCameraPermission: Boolean,
    callbacks: CameraCallbacks,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier, topBar = {
        // Encabezado superior
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .fillMaxWidth()
                .background(Color.Black)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(Modifier.clickable(onClick = callbacks.onBack)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.back), color = Color.White)
            }
            TextButton(onClick = callbacks.onSkipClick) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = stringResource(R.string.skip),
                    tint = Color.White,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.skip),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.Black),
        ) {
            // Vista de cámara
            if (isCameraPermission) {
                CameraPreview(
                    onImageCaptureForAnalysis = callbacks.processImage,
                    onImageCapture = callbacks.saveImage,
                    modifier = Modifier.size(412.dp, 648.dp),
                )
            }

            // Status cargando
            if (isProcessing) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            // Marco gris claro cuadrado
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(265.dp, 385.dp)
                    .offset(y = (55).dp)
                    .border(2.dp, Color.LightGray, shape = RectangleShape),
            )

            // Text reconocido
            if (!isProcessing) {
                Text(
                    "¿Es Correcto?",
                    color = Color.White,
                    modifier = Modifier.offset(y = (-180).dp).align(Alignment.BottomCenter),
                )
            }

            Text(
                recognizedText,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-120).dp)
                    .align(Alignment.BottomCenter),
            )

            CameraControllers(
                showControls = showControls,
                reset = callbacks.reset,
                onConfirm = callbacks.onConfirm,
                modifier = Modifier.offset(y = (-30).dp),
            )
        }
    }
}

@Composable
fun BoxScope.CameraControllers(
    showControls: Boolean,
    reset: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (showControls) {
        Row(
            modifier = modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = onConfirm) {
                Text("Continuar")
            }
            Spacer(Modifier.width(8.dp))
            FilledTonalButton(onClick = reset) {
                Text("Reintentar")
            }
        }
    }
}

@Preview
@Composable
private fun CamaraPreview() {
    WheelVaultTheme {
        CameraLensContent(
            recognizedText = "Hola",
            isProcessing = false,
            showControls = true,
            isCameraPermission = false,
            callbacks = CameraCallbacks(
                reset = {},
                onConfirm = {},
                onBack = {},
                onSkipClick = {},
                processImage = {},
                saveImage = {},
            ),
        )
    }
}
