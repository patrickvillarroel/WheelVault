package io.github.patrickvillarroel.wheel.vault.domain.model

import kotlin.uuid.Uuid

data class CarItem(
    val model: String,
    val year: Int,
    val manufacturer: String,
    val brand: String,
    val id: Uuid = Uuid.random(),
    val images: Set<Any> = setOfNotNull(EmptyImage),
    val quantity: Int = 0,
    val isFavorite: Boolean = false,
    val availableForTrade: Boolean = false,
    val description: String? = null,
    val category: String? = null,
    val tradeMessage: String? = null,
    val imageUrl: Any = images.first(),
) {
    constructor(
        model: String,
        year: Int,
        manufacturer: String,
        quantity: Int = 0,
        imageUrl: Any,
        isFavorite: Boolean = false,
        availableForTrade: Boolean = false,
    ) : this(
        model = model,
        year = year,
        brand = manufacturer,
        manufacturer = manufacturer,
        quantity = quantity,
        images = setOf(imageUrl),
        isFavorite = isFavorite,
        availableForTrade = availableForTrade,
    )

    init {
        require(images.isNotEmpty()) { "Images cannot be empty" }
        require(images.distinct().size == images.size) { "Images must be unique" }
        require(imageUrl in images) { "Primary image URL must be one of the provided images" }
        require(quantity >= 0) { "Quantity cannot be negative" }
        require(year > 0) { "Year must be greater than 0" }
        require(model.isNotBlank()) { "Model cannot be blank" }
        require(manufacturer.isNotBlank()) { "Manufacturer cannot be blank" }
        require(brand.isNotBlank()) { "Brand cannot be blank" }
    }

    fun toBuilder() = Builder(
        model = model,
        year = year,
        manufacturer = manufacturer,
        quantity = quantity,
        brand = brand,
        description = description,
        category = category,
        tradeMessage = tradeMessage,
        images = images,
        isFavorite = isFavorite,
        availableForTrade = availableForTrade,
        id = id,
    )

    data class Builder(
        val model: String? = null,
        val year: Int? = null,
        val manufacturer: String? = null,
        val quantity: Int = 0,
        val brand: String? = null,
        val description: String? = null,
        val category: String? = null,
        val tradeMessage: String? = null,
        val images: Set<Any> = setOf(),
        val isFavorite: Boolean = false,
        val availableForTrade: Boolean? = null,
        val id: Uuid? = null,
    ) {
        fun build(): CarItem? {
            return CarItem(
                id = this.id ?: Uuid.random(),
                model = model ?: return null,
                year = year ?: return null,
                manufacturer = manufacturer ?: brand ?: return null,
                quantity = quantity,
                brand = brand ?: manufacturer ?: return null,
                images = images.takeIf { it.isNotEmpty() } ?: setOfNotNull(EmptyImage),
                isFavorite = isFavorite,
                availableForTrade = availableForTrade ?: false,
                description = this.description,
                category = this.category,
                tradeMessage = this.tradeMessage,
            )
        }

        fun removeEmptyProperties() = this.copy(
            model = model?.takeIf { it.isNotBlank() },
            year = year?.takeIf { it > 0 },
            manufacturer = manufacturer?.takeIf { it.isNotBlank() },
            quantity = quantity.takeIf { it >= 0 } ?: 0,
            brand = brand?.takeIf { it.isNotBlank() },
            description = description?.takeIf { it.isNotBlank() },
            category = category?.takeIf { it.isNotBlank() },
            tradeMessage = tradeMessage?.takeIf { it.isNotBlank() },
            images = images.filterNot { it == EmptyImage }.takeIf { it.isNotEmpty() }?.toSet() ?: emptySet(),
            isFavorite = isFavorite,
            availableForTrade = availableForTrade,
            id = id,
        )
    }

    companion object {
        val EmptyImage = io.github.patrickvillarroel.wheel.vault.R.drawable.car_add
    }
}
