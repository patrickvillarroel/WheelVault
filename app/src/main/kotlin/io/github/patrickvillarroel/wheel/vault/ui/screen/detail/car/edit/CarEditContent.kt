package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BackTextButton
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.FavoriteIcon
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeroImageCarousel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.InterceptedHeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuHeader
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.RedOutlinedTextField
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

private val carEditLogger = Logger.withTag("CarEditContent")

@Composable
fun CarEditContent(
    initial: CarItem.Builder,
    isEditAction: Boolean,
    onAddPictureClick: (CarItem.Builder) -> Unit,
    onConfirmClick: (CarItem.Builder) -> Unit,
    manufacturerList: List<String>,
    headersBackCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
) {
    // Internal states
    var car by remember(initial) {
        carEditLogger.i { "recomposed with $initial" }
        mutableStateOf(initial)
    }
    var marca by rememberSaveable(initial.brand) { mutableStateOf(initial.brand ?: "") }
    var modelo by rememberSaveable(initial.model) { mutableStateOf(initial.model ?: "") }
    var descripcion by rememberSaveable(initial.description) { mutableStateOf(initial.description ?: "") }
    var manufacturerExpanded by rememberSaveable { mutableStateOf(false) }
    var year by rememberSaveable(initial.year) { mutableStateOf(initial.year?.toString() ?: "") }
    var cantidad by rememberSaveable(initial.quantity) { mutableStateOf(initial.quantity.toString()) }
    var categoria by rememberSaveable(initial.category) { mutableStateOf(initial.category ?: "") }
    val imagenes = remember(initial.images) {
        mutableStateSetOf<Any>().also { it.addAll(initial.images + R.drawable.car_add) }
    }
    var showCancelDialog by rememberSaveable { mutableStateOf(false) }
    val headerCallbacks = remember {
        InterceptedHeaderBackCallbacks(
            headersBackCallbacks,
            { _, action ->
                if (showCancelDialog) {
                    action()
                } else {
                    showCancelDialog = true
                }
            },
        )
    }

    BackHandler { showCancelDialog = true }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MenuHeader(headerCallbacks) {
                BackTextButton(headerCallbacks.onBackClick)
            }
        },
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            item { HeroImageCarousel(imagenes) }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = if (isEditAction) {
                            stringResource(R.string.edit_car)
                        } else {
                            stringResource(R.string.add_car)
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                    FavoriteIcon(
                        isFavorite = car.isFavorite,
                        onFavoriteToggle = {
                            car = car.copy(isFavorite = it)
                        },
                    )
                }
            }

            item {
                RedOutlinedTextField(
                    value = marca,
                    onValueChange = {
                        marca = it
                        car = car.copy(brand = it.takeIf(String::isNotBlank)?.trim())
                    },
                    isError = marca.isBlank() || car.manufacturer.isNullOrBlank(),
                    label = stringResource(R.string.brand),
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RedOutlinedTextField(
                        value = modelo,
                        onValueChange = {
                            modelo = it
                            car = car.copy(model = it)
                        },
                        isError = modelo.isBlank(),
                        label = stringResource(R.string.model),
                        modifier = Modifier.weight(1f),
                    )
                    RedOutlinedTextField(
                        value = year,
                        onValueChange = {
                            year = it
                            car = car.copy(year = it.trim().takeIf(String::isNotBlank)?.toIntOrNull())
                        },
                        isError = year.isBlank() || year.toIntOrNull() == null,
                        label = stringResource(R.string.year),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            }

            item {
                RedOutlinedTextField(
                    value = descripcion,
                    onValueChange = {
                        descripcion = it
                        car = car.copy(description = it.takeIf(String::isNotBlank)?.trim())
                    },
                    label = stringResource(R.string.description),
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        val textFieldState = rememberTextFieldState(
                            manufacturerList.firstOrNull { it == car.manufacturer } ?: manufacturerList[0],
                        )

                        ExposedDropdownMenuBox(
                            expanded = manufacturerExpanded,
                            onExpandedChange = { manufacturerExpanded = it },
                        ) {
                            OutlinedTextField(
                                state = textFieldState,
                                readOnly = true,
                                lineLimits = TextFieldLineLimits.SingleLine,
                                label = { Text(stringResource(R.string.manufacture)) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = manufacturerExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Red,
                                    unfocusedBorderColor = Color.Red,
                                    focusedLabelColor = Color.Red,
                                    unfocusedLabelColor = Color.Gray,
                                ),
                            )
                            ExposedDropdownMenu(
                                expanded = manufacturerExpanded,
                                onDismissRequest = { manufacturerExpanded = false },
                            ) {
                                manufacturerList.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                                        onClick = {
                                            textFieldState.setTextAndPlaceCursorAtEnd(option)
                                            car = car.copy(manufacturer = option)
                                            manufacturerExpanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }
                    }

                    RedOutlinedTextField(
                        value = cantidad,
                        onValueChange = {
                            cantidad = it
                            car = car.copy(
                                quantity = it.takeIf(String::isNotBlank)
                                    ?.toIntOrNull()
                                    ?.takeIf { n -> n >= 0 } ?: 0,
                            )
                        },
                        isError = cantidad.isBlank() || cantidad.toIntOrNull() == null || cantidad.toInt() < 0,
                        label = stringResource(R.string.quantity),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            }

            item {
                RedOutlinedTextField(
                    value = categoria,
                    onValueChange = {
                        categoria = it
                        car = car.copy(category = it.takeIf(String::isNotBlank)?.trim())
                    },
                    label = stringResource(R.string.category),
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.image_extras), style = MaterialTheme.typography.titleMedium)
                Button(
                    onClick = { onAddPictureClick(car.removeEmptyProperties()) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.add_variant), color = Color.White)
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Button(
                        onClick = { showCancelDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color.White)
                    }

                    Button(
                        onClick = { onConfirmClick(car.removeEmptyProperties()) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.White)
                    }
                }
            }
        }

        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text(stringResource(R.string.descart_progress)) },
                text = { Text(stringResource(R.string.descart_progress_text)) },
                confirmButton = {
                    Button(
                        onClick = {
                            showCancelDialog = false
                            headersBackCallbacks.onBackClick()
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                    ) {
                        Text(stringResource(R.string.yes_exit))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                },
            )
        }
    }
}

@Preview
@Composable
private fun EditPreview() {
    val carDetailPartial = remember { CarItem.Builder() }

    WheelVaultTheme {
        CarEditContent(
            initial = carDetailPartial,
            onConfirmClick = {},
            isEditAction = true,
            onAddPictureClick = {},
            manufacturerList = BrandViewModel.manufacturerList,
            headersBackCallbacks = HeaderBackCallbacks.default,
        )
    }
}
