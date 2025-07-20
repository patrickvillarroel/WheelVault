package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BackTextButton
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.FavoriteIcon
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeroImageCarousel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.InterceptedHeaderBackCallbacks
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuHeader
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.RedOutlinedTextField
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun CarEditContent(
    initial: CarItem.Partial,
    isEditAction: Boolean,
    onAddPictureClick: (CarItem.Partial) -> Unit,
    onConfirmClick: (CarItem.Partial) -> Unit,
    headersBackCallbacks: HeaderBackCallbacks,
    modifier: Modifier = Modifier,
) {
    // Internal states
    var car by remember(initial) {
        Log.i("CarEditContent", "recomposer with $initial")
        mutableStateOf(initial)
    }
    var marca by rememberSaveable(initial.brand) { mutableStateOf(initial.brand ?: "") }
    var modelo by rememberSaveable(initial.model) { mutableStateOf(initial.model ?: "") }
    var descripcion by rememberSaveable(initial.description) { mutableStateOf(initial.description ?: "") }
    var manufacturer by rememberSaveable(initial.manufacturer) { mutableStateOf(initial.manufacturer ?: "") }
    var year by rememberSaveable(initial.year) { mutableStateOf(initial.year?.toString() ?: "") }
    var cantidad by rememberSaveable(initial.quantity) { mutableStateOf(initial.quantity.toString()) }
    var categoria by rememberSaveable(initial.category) { mutableStateOf(initial.category ?: "") }
    val imagenes by remember(initial.images) { mutableStateOf(initial.images + R.drawable.car_add) }
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
                        text = if (isEditAction) "Editar Carrito" else "Agregar Carrito",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                    FavoriteIcon(car.isFavorite, onFavoriteToggle = {
                        car = car.copy(isFavorite = it)
                    })
                }
            }

            item {
                RedOutlinedTextField(marca, {
                    marca = it
                    car = car.copy(brand = it.takeIf(String::isNotBlank)?.trim())
                }, "Marca")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RedOutlinedTextField(modelo, {
                        modelo = it
                        car = car.copy(model = it)
                    }, "Modelo", Modifier.weight(1f))
                    RedOutlinedTextField(
                        year,
                        {
                            year = it
                            car = car.copy(year = it.trim().takeIf(String::isNotBlank)?.toIntOrNull())
                        },
                        "Año",
                        Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            }

            item {
                RedOutlinedTextField(descripcion, {
                    descripcion = it.trim()
                    car = car.copy(description = it.takeIf(String::isNotBlank)?.trim())
                }, "Descripción")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RedOutlinedTextField(manufacturer, {
                        manufacturer = it
                        car = car.copy(manufacturer = it.takeIf(String::isNotBlank)?.trim())
                    }, "Manufactura", Modifier.weight(1f))
                    RedOutlinedTextField(
                        cantidad,
                        {
                            cantidad = it
                            car = car.copy(quantity = it.takeIf(String::isNotBlank)?.toIntOrNull() ?: 0)
                        },
                        "Cantidad",
                        Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            }

            item {
                RedOutlinedTextField(
                    categoria,
                    {
                        categoria = it
                        car = car.copy(category = it.takeIf(String::isNotBlank)?.trim())
                    },
                    "Categoría",
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("Imágenes Extras")
                Button(
                    onClick = { onAddPictureClick(car.removeEmptyProperties()) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text("Agregar", color = Color.White)
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
                title = { Text("¿Descartar cambios?") },
                text = { Text("Los cambios que hiciste se perderán. ¿Estás seguro de que quieres salir?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showCancelDialog = false
                            headersBackCallbacks.onBackClick()
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                    ) {
                        Text("Sí, salir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) {
                        Text("Cancelar")
                    }
                },
            )
        }
    }
}

@Preview
@Composable
private fun EditPreview() {
    val carDetailPartial = remember { CarItem.Partial() }

    WheelVaultTheme {
        CarEditContent(
            initial = carDetailPartial,
            onConfirmClick = {},
            isEditAction = true,
            onAddPictureClick = {},
            headersBackCallbacks = HeaderBackCallbacks(
                onBackClick = {},
                onProfileClick = {},
                onGarageClick = {},
                onFavoritesClick = {},
                onStatisticsClick = {},
            ),
        )
    }
}
