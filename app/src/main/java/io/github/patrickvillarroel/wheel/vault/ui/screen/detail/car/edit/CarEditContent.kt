package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.BackTextButton
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.FavoriteIcon
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeroImageCarousel
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.MenuHeader
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.RedOutlinedTextField
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

// TODO todos los callbacks deben interceptarse para el cancel modal
@Composable
fun CarEditContent(
    carDetailPartial: CarItem.Partial,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onGarageClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onAddPictureClick: (Any) -> Any,
    onConfirmClick: (CarItem.Partial) -> Unit,
    isEditAction: Boolean,
    modifier: Modifier = Modifier,
) {
    // Internal states
    var car by remember { mutableStateOf(carDetailPartial) }
    var marca by rememberSaveable { mutableStateOf(carDetailPartial.brand ?: "") }
    var modelo by rememberSaveable { mutableStateOf(carDetailPartial.model ?: "") }
    var descripcion by rememberSaveable { mutableStateOf(carDetailPartial.description ?: "") }
    var manufacturer by rememberSaveable { mutableStateOf(carDetailPartial.manufacturer ?: "") }
    var cantidad by rememberSaveable { mutableStateOf(carDetailPartial.quantity.toString()) }
    var categoria by rememberSaveable { mutableStateOf(carDetailPartial.category ?: "") }
    val imagenes by rememberSaveable { mutableStateOf(carDetailPartial.images) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MenuHeader(onProfileClick, onGarageClick, onFavoritesClick, onStatisticsClick) {
                BackTextButton(onBackClick)
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
                    FavoriteIcon(carDetailPartial.isFavorite, onFavoriteToggle = {
                        car = car.copy(isFavorite = it)
                    })
                }
            }

            item {
                RedOutlinedTextField(marca, {
                    marca = it
                    car = car.copy(brand = it)
                }, "Marca")
            }
            item {
                RedOutlinedTextField(modelo, {
                    modelo = it
                    car = car.copy(model = it)
                }, "Modelo")
            }
            item {
                RedOutlinedTextField(descripcion, {
                    descripcion = it
                    car = car.copy(description = it)
                }, "Descripción")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RedOutlinedTextField(manufacturer, {
                        manufacturer = it
                        car = car.copy(manufacturer = it)
                    }, "Manufactura", Modifier.weight(1f))
                    RedOutlinedTextField(
                        cantidad,
                        {
                            cantidad = it
                            car = car.copy(quantity = it.toIntOrNull() ?: 0)
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
                        car = car.copy(category = it)
                    },
                    "Categoría",
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("Imágenes Extra")
                Button(
                    // car = car.copy(images = car.images + onAddPictureClick(imagenes))
                    onClick = { /* TODO Lanzar modal o dropdown menu elegir camara o galeria, lanzar el intent */ },
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
                    OutlinedButton(onClick = { /* TODO Add cancel confirm modal */ }) {
                        Icon(Icons.Default.Close, null, Modifier.size(32.dp))
                    }

                    OutlinedButton(onClick = { onConfirmClick(carDetailPartial) }) {
                        Icon(Icons.Default.Check, null, Modifier.size(32.dp), Color.Red)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun EditPreview() {
    val carDetailPartial = remember { CarItem.Partial() }

    WheelVaultTheme {
        CarEditContent(
            carDetailPartial = carDetailPartial,
            onBackClick = {},
            onProfileClick = {},
            onGarageClick = {},
            onFavoritesClick = {},
            onStatisticsClick = {},
            onConfirmClick = {},
            onAddPictureClick = { "image1.jpg" },
            isEditAction = true,
        )
    }
}
