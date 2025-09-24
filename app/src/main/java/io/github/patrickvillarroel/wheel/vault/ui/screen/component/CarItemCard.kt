package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.theme.WheelVaultTheme

@Composable
fun CarItemCard(
    carItem: CarItem,
    onClick: () -> Unit,
    onFavoriteToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    favoriteIcon: Boolean = true,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A3540)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AsyncImage(
                model = carItem.imageUrl,
                contentDescription = carItem.model,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(100.dp)
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(8.dp)),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = carItem.model,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp,
                    )
                    if (favoriteIcon) {
                        FavoriteIcon(
                            isFavorite = carItem.isFavorite,
                            onFavoriteToggle = onFavoriteToggle,
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.height(4.dp), color = Color(0xFFE42E31))

                Text(
                    text = stringResource(R.string.year_of, carItem.year),
                    color = Color.White,
                    fontSize = 13.sp,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.manufacture_of, carItem.manufacturer),
                    color = Color.White,
                    fontSize = 13.sp,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.quantity_of, carItem.quantity),
                    color = Color.White,
                    fontSize = 13.sp,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CardItemPreview() {
    val car = remember {
        CarItem(
            model = "Ford Mustang GTD",
            year = 2025,
            brand = "Hot Wheels",
            manufacturer = "HotWheels",
            quantity = 2,
            images = setOf(
                "https://tse1.mm.bing.net/th/id/OIP.zfsbW7lEIwYgeUt7Fd1knwHaHg?rs=1&pid=ImgDetMain&o=7&rm=3",
            ),
            isFavorite = true,
        )
    }

    WheelVaultTheme {
        CarItemCard(carItem = car, onClick = {}, onFavoriteToggle = {}, modifier = Modifier.padding(16.dp))
    }
}
