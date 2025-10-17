package io.github.patrickvillarroel.wheel.vault.ui.screen.camera

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
        TopAppBar(
            title = {},
            navigationIcon = {
                TextButton(
                    onClick = callbacks.onBack,
                    modifier = Modifier.padding(start = 16.dp),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        stringResource(R.string.back),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.back), color = Color.White)
                }
            },
            actions = {
                TextButton(onClick = callbacks.onSkip) {
                    Icon(
                        Icons.Default.FastForward,
                        stringResource(R.string.skip),
                        tint = Color.White,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.skip),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.White,
            ),
        )
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
                    instructionText = stringResource(R.string.camera_permission_requesting)
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
                    instructionText = stringResource(R.string.confirm_text)
                }

                is CameraUiState.CapturingImage -> {
                    showCameraPreview = true
                    instructionText = stringResource(R.string.capturing_image_instruction)
                }

                is CameraUiState.ConfirmCapturedImage -> {
                    capturedImageForConfirmation = uiState.capturedImage
                    instructionText = stringResource(R.string.confirm_image)
                }

                is CameraUiState.Completed -> {
                    instructionText = stringResource(R.string.completed)
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
                    triggerImageAnalysis = uiState is CameraUiState.ProcessingText,
                    triggerImageCapture = triggerImageCapture,
                    modifier = Modifier.size(412.dp, 648.dp).align(Alignment.Center).offset(y = (-55).dp),
                )
            } else if (capturedImageForConfirmation != null) {
                Image(
                    capturedImageForConfirmation.asImageBitmap(),
                    stringResource(R.string.captured_image),
                    modifier = Modifier.size(412.dp, 648.dp).align(Alignment.Center).offset(y = (-55).dp),
                )
            }

            CameraIndication(
                instructionText = instructionText,
                showProcessingIndicator = showProcessingIndicator,
                showCameraPreview = showCameraPreview,
                recognizedTextForConfirmation = recognizedTextForConfirmation,
                capturedImageForConfirmation = capturedImageForConfirmation,
            )

            CameraControls(uiState, callbacks)
        }
    }
}

@Composable
fun BoxScope.CameraIndication(
    instructionText: String,
    showProcessingIndicator: Boolean,
    showCameraPreview: Boolean,
    recognizedTextForConfirmation: String?,
    capturedImageForConfirmation: Bitmap?,
    modifier: Modifier = Modifier,
) {
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
        modifier = modifier
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
}

@Composable
fun BoxScope.CameraControls(uiState: CameraUiState, callbacks: CameraCallbacks, modifier: Modifier = Modifier) {
    // Buttons at the bottom
    Box(
        modifier = modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 30.dp),
    ) {
        when (uiState) {
            is CameraUiState.ConfirmRecognizedText -> {
                Row {
                    Button(onClick = callbacks.onTextRecognitionConfirmed) {
                        Text(stringResource(R.string.yes_confirm))
                    }
                    Spacer(Modifier.width(8.dp))
                    FilledTonalButton(onClick = callbacks.onTextRecognitionRetry) {
                        Text(stringResource(R.string.no_retry))
                    }
                }
            }

            is CameraUiState.CapturingImage -> {
                FilledIconButton(
                    onClick = callbacks.onTakePictureRequest,
                    modifier = Modifier.size(72.dp),
                ) {
                    Icon(
                        Icons.Filled.CameraAlt,
                        stringResource(R.string.take_picture),
                    )
                }
            }

            is CameraUiState.ConfirmCapturedImage -> {
                Row {
                    Button(onClick = callbacks.onCapturedImageConfirmation) {
                        Text(stringResource(R.string.yes_confirm))
                    }
                    Spacer(Modifier.width(8.dp))
                    FilledTonalButton(onClick = callbacks.onCapturedImageRetry) {
                        Text(stringResource(R.string.no_retry))
                    }
                }
            }

            else -> {
                /* No controls for other states or handled by skip/back */
            }
        }
    }
}
