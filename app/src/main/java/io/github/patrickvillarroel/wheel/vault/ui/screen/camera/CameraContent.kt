package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R

@Composable
fun CameraLensContent(
    uiState: CameraUiState,
    callbacks: CameraCallbacks,
    triggerImageCapture: Boolean,
    modifier: Modifier = Modifier,
) {
    BackHandler {
        callbacks.onBack()
    }
    Scaffold(modifier, topBar = {
        Row(
            modifier = Modifier
                .windowInsetsPadding(TopAppBarDefaults.windowInsets)
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
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.back), color = Color.White)
            }
            TextButton(onClick = callbacks.onSkip) {
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
            contentAlignment = Alignment.Center,
        ) {
            var showCameraPreview = false
            val instructionText: String
            var recognizedTextForConfirmation: String? = null
            var capturedImageForConfirmation: Bitmap? = null
            var showProcessingIndicator = false

            when (uiState) {
                is CameraUiState.RequestingPermission -> {
                    instructionText = "Solicitando permiso de cámara..."
                    showProcessingIndicator = true
                }

                is CameraUiState.ProcessingText -> {
                    showCameraPreview = true
                    instructionText = uiState.instructionText
                    showProcessingIndicator = true // Show while processing or waiting for text
                }

                is CameraUiState.ConfirmRecognizedText -> {
                    showCameraPreview = true
                    recognizedTextForConfirmation = uiState.recognizedText
                    instructionText = "¿Es Correcto?"
                }

                is CameraUiState.CapturingImage -> {
                    showCameraPreview = true
                    instructionText = "Encuadre el modelo y presione el botón de la cámara."
                }

                is CameraUiState.ConfirmCapturedImage -> {
                    capturedImageForConfirmation = uiState.capturedImage
                    instructionText = "¿Confirmar esta imagen?"
                }

                is CameraUiState.Completed -> {
                    instructionText = "Completado."
                    callbacks.onAddDetail(uiState.currentText, uiState.currentImage)
                }
            }

            // Camera Preview or Image Display
            if (showCameraPreview && capturedImageForConfirmation == null) {
                CameraPreview(
                    onImageCaptureForAnalysis = if (uiState is CameraUiState.ProcessingText) {
                        callbacks.processImageForTextAnalysis
                    } else {
                        { it.close() }
                    },
                    onImageCapture = callbacks.onCapturedImageProvided,
                    triggerImageCapture = triggerImageCapture,
                    modifier = Modifier.size(412.dp, 648.dp).align(Alignment.Center),
                )
            } else if (capturedImageForConfirmation != null) {
                Image(
                    capturedImageForConfirmation.asImageBitmap(),
                    contentDescription = "Confirmación de imagen capturada",
                    modifier = Modifier.size(412.dp, 648.dp).align(Alignment.Center),
                )
            }

            // Processing Indicator
            if (showProcessingIndicator &&
                recognizedTextForConfirmation == null &&
                capturedImageForConfirmation == null
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            // Recognition/Capture Frame
            if (showCameraPreview) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(265.dp, 385.dp)
                        .offset(y = (55).dp)
                        .border(2.dp, Color.LightGray, shape = RectangleShape),
                )
            }

            // Instruction and Recognized Text Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp), // Space for buttons
            ) {
                if (instructionText.isNotBlank()) {
                    Text(
                        instructionText,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                if (recognizedTextForConfirmation != null) {
                    Text(
                        recognizedTextForConfirmation,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            // Buttons at the bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp),
            ) {
                when (uiState) {
                    is CameraUiState.ConfirmRecognizedText -> {
                        Row {
                            Button(onClick = callbacks.onTextRecognitionConfirmed) { Text("Sí, continuar") }
                            Spacer(Modifier.width(8.dp))
                            FilledTonalButton(onClick = callbacks.onTextRecognitionRetry) { Text("Reintentar") }
                        }
                    }

                    is CameraUiState.CapturingImage -> {
                        FilledIconButton(
                            onClick = callbacks.onTakePictureRequest, // This now signals ViewModel
                            modifier = Modifier.size(72.dp), // Larger, circular button
                        ) {
                            Icon(
                                Icons.Filled.CameraAlt,
                                "Capturar Imagen",
                            )
                        }
                    }

                    is CameraUiState.ConfirmCapturedImage -> {
                        Row {
                            Button(onClick = callbacks.onCapturedImageConfirmation) { Text("Confirmar Imagen") }
                            Spacer(Modifier.width(8.dp))
                            FilledTonalButton(onClick = callbacks.onCapturedImageRetry) { Text("Reintentar Captura") }
                        }
                    }

                    else -> {
                        /* No controls for other states or handled by skip/back */
                    }
                }
            }
        }
    }
}
