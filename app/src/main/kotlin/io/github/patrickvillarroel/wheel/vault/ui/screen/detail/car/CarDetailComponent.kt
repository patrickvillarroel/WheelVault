package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import io.github.patrickvillarroel.wheel.vault.R
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.RaceDivider

@Suppress("ktlint:standard:function-naming")
fun LazyListScope.CarDetail(carDetail: CarItem) {
    item {
        Text(stringResource(R.string.brand), style = MaterialTheme.typography.labelLarge)
        Text(carDetail.brand, style = MaterialTheme.typography.bodyLarge)
        RaceDivider()
    }

    item {
        Text(stringResource(R.string.model), style = MaterialTheme.typography.labelLarge)
        Text(carDetail.model, style = MaterialTheme.typography.bodyLarge)
        RaceDivider()
    }

    item {
        Text(stringResource(R.string.year), style = MaterialTheme.typography.labelLarge)
        Text(carDetail.year.toString(), style = MaterialTheme.typography.bodyLarge)
        RaceDivider()
    }

    item {
        Text(stringResource(R.string.manufacture), style = MaterialTheme.typography.labelLarge)
        Text(carDetail.manufacturer, style = MaterialTheme.typography.bodyLarge)
        RaceDivider()
    }

    item {
        Text(stringResource(R.string.category), style = MaterialTheme.typography.labelLarge)
        Text(carDetail.category ?: "--", style = MaterialTheme.typography.bodyLarge)
        RaceDivider()
    }

    item {
        Text(stringResource(R.string.description), style = MaterialTheme.typography.labelLarge)
        Text(carDetail.description ?: "--", style = MaterialTheme.typography.bodyLarge)
        RaceDivider()
    }

    item {
        Text(
            stringResource(R.string.quantity_of, carDetail.quantity),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        RaceDivider()
    }
}
