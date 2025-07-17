package io.github.patrickvillarroel.wheel.vault.ui.screen.detail.brand

import androidx.compose.runtime.Immutable
import io.github.patrickvillarroel.wheel.vault.domain.model.Brand
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.ui.screen.component.HeaderBackCallbacks
import java.util.UUID

@Immutable
open class BrandInfo(
    val brand: Brand,
    val carCollection: List<CarItem>,
    val animationKey: String = "brand-${brand.id}",
) {
    val iconDetail = brand.image to brand.contentDescription
}

open class BrandDetail(
    brandInfo: BrandInfo,
    val onAddClick: () -> Unit,
    val onCarClick: (UUID) -> Unit,
    val onFavoriteToggle: (CarItem, Boolean) -> Unit,
    val headerBackCallbacks: HeaderBackCallbacks,
) : BrandInfo(brandInfo.brand, brandInfo.carCollection, brandInfo.animationKey) {
    constructor(
        brand: Brand,
        carCollection: List<CarItem>,
        onAddClick: () -> Unit,
        onCarClick: (UUID) -> Unit,
        onFavoriteToggle: (CarItem, Boolean) -> Unit,
        headerBackCallbacks: HeaderBackCallbacks,
        animationKey: String = "brand-${brand.id}",
    ) : this(
        BrandInfo(brand, carCollection, animationKey),
        onAddClick,
        onCarClick,
        onFavoriteToggle,
        headerBackCallbacks,
    )
}
