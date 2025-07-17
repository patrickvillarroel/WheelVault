package io.github.patrickvillarroel.wheel.vault.domain.model

import java.util.UUID

data class CarItem(
    val id: UUID = UUID.randomUUID(),
    val model: String,
    val year: Int,
    val manufacturer: String,
    val brand: String,
    val images: Set<Any> = setOf(EmptyImage),
    val quantity: Int = 0,
    val isFavorite: Boolean = false,
    val description: String? = null,
    val category: String? = null,
    val imageUrl: Any = images.first(),
) {
    constructor(
        model: String,
        year: Int,
        manufacturer: String,
        quantity: Int = 0,
        imageUrl: Any,
        isFavorite: Boolean = false,
    ) : this(
        model = model,
        year = year,
        brand = manufacturer,
        manufacturer = manufacturer,
        quantity = quantity,
        images = setOf(imageUrl),
        isFavorite = isFavorite,
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

    fun toPartial() = Partial(
        model = model,
        year = year,
        manufacturer = manufacturer,
        quantity = quantity,
        brand = brand,
        description = description,
        category = category,
        images = images,
        isFavorite = isFavorite,
    )

    data class Partial(
        val model: String? = null,
        val year: Int? = null,
        val manufacturer: String? = null,
        val quantity: Int = 0,
        val brand: String? = null,
        val description: String? = null,
        val category: String? = null,
        val images: Set<Any> = setOf(),
        val isFavorite: Boolean = false,
    ) {
        fun toCarItem(): CarItem? {
            return CarItem(
                model = model ?: return null,
                year = year ?: return null,
                manufacturer = manufacturer ?: brand ?: return null,
                quantity = quantity,
                brand = brand ?: manufacturer ?: return null,
                images = images.takeIf { it.isNotEmpty() } ?: return null,
                isFavorite = isFavorite,
            )
        }
    }

    data object EmptyImage
}
