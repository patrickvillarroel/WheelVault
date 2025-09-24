package io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarCrash
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BackTextButton
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeroImageCarousel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuHeader
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarDetail
import io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.CarDetailCallbacks

@Composable
fun ExchangeCarOffer(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    carDetail: CarItem,
    callbacks: CarDetailCallbacks,
    onExchangeClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var requisitoState: String? by remember { mutableStateOf(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MenuHeader(callbacks.headersBackCallbacks) {
                BackTextButton(onBack = callbacks.headersBackCallbacks.onBackClick)
            }
        },
        floatingActionButton = {
            TextButton(
                onClick = { showBottomSheet = true },
                elevation = ButtonDefaults.buttonElevation(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE42E31)),
            ) {
                Text(
                    stringResource(R.string.offer_action),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp),
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
                    Text(
                        stringResource(R.string.information_of_car),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }

                CarDetail(carDetail)

                item {
                    Box(Modifier.height(100.dp))
                }
            }

            if (showBottomSheet) {
                AddRequirementBottomSheet(
                    onDismiss = { showBottomSheet = false },
                    onConfirm = { requisito ->
                        showBottomSheet = false
                        requisitoState = requisito.takeIf { it.isNotBlank() }
                        showConfirmationDialog = true
                    },
                )
            }

            if (showConfirmationDialog) {
                ConfirmationDialog(
                    onDismissRequest = { showConfirmationDialog = false },
                    onConfirm = {
                        showConfirmationDialog = false
                        onExchangeClick(requisitoState)
                    },
                )
            }
        }
    }
}

@Composable
private fun ConfirmationDialog(onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1C1C1E),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Icono
                Icon(
                    imageVector = Icons.Default.CarCrash,
                    contentDescription = null,
                    tint = Color(0xFFE42E31),
                    modifier = Modifier.size(48.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Titulo
                Text(
                    text = stringResource(id = R.string.warning),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mensaje
                Text(
                    text = stringResource(id = R.string.exchange_confirmation_message),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f),
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE42E31),
                            contentColor = Color.White,
                        ),
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF28282A),
                            contentColor = Color.White,
                        ),
                    ) {
                        Text(stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
private fun AddRequirementBottomSheet(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var requisito by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1C1E),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Ícono
            Icon(
                Icons.Default.CarCrash,
                contentDescription = null,
                tint = Color(0xFFE42E31),
                modifier = Modifier.size(32.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Título
            Text(
                text = stringResource(R.string.write_request_question),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto
            OutlinedTextField(
                value = requisito,
                onValueChange = { requisito = it },
                placeholder = { Text(stringResource(R.string.write_here), color = Color.Gray) },
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE42E31),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFE42E31),
                ),
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE42E31),
                        contentColor = Color.White,
                    ),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.cancel))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { onConfirm(requisito.trim()) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White,
                    ),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.confirm))
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
            ExchangeCarOffer(
                sharedTransitionScope = this@SharedTransitionScope,
                animatedVisibilityScope = this,
                carDetail = carDetail,
                callbacks = callbacks,
                onExchangeClick = {},
                modifier = it,
            )
        }
    }
}
