/** This file contain a extension only callable in [EntryProviderBuilder] to convert a domain model to navigation model */
package io.github.patrickvillarroel.wheel.vault.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem

/** Extension function to convert a [CarItem] to a [NavigationKeys.CarEdit], only callable in a [EntryProviderBuilder] */
context(_: EntryProviderBuilder<NavKey>)
fun CarItem.toCarEdit(): NavigationKeys.CarEdit = this.toPartial().toCarEdit()

/** Extension function to convert a [CarItem.Partial] to a [NavigationKeys.CarEdit], only callable in a [EntryProviderBuilder] */
context(_: EntryProviderBuilder<NavKey>)
fun CarItem.Partial.toCarEdit(): NavigationKeys.CarEdit {
    val partial = this
    return NavigationKeys.CarEdit(
        model = partial.model,
        brand = partial.brand,
        year = partial.year,
        quantity = partial.quantity,
        manufacturer = partial.manufacturer,
        isFavorite = partial.isFavorite,
        images = partial.images.map { it.toString() },
        description = partial.description,
        category = partial.category,
    )
}

/** Extension function to convert a [NavigationKeys.CarEdit] to [CarItem.Partial], especial only callable in a [EntryProviderBuilder] */
context(_: EntryProviderBuilder<NavKey>)
fun NavigationKeys.CarEdit.toCarPartial(): CarItem.Partial {
    val partial = this
    return CarItem.Partial(
        model = partial.model,
        brand = partial.brand,
        year = partial.year,
        quantity = partial.quantity,
        manufacturer = partial.manufacturer,
        isFavorite = partial.isFavorite,
        images = partial.images,
        description = partial.description,
        category = partial.category,
    )
}
