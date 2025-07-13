package io.github.patrickvillarroel.wheel.vault.domain.model

import kotlin.random.Random

data class CarItem(
    // TODO
    val id: Int = Random.nextInt(),
    val model: String,
    val year: Int,
    val manufacturer: String,
    val brand: String,
    val images: List<String>,
    val quantity: Int = 0,
    val isFavorite: Boolean = false,
    val description: String? = null,
    val category: String? = null,
    val imageUrl: String = images.first(),
) {
    constructor(
        model: String,
        year: Int,
        manufacturer: String,
        quantity: Int = 0,
        imageUrl: String,
        isFavorite: Boolean = false,
    ) : this(
        model = model,
        year = year,
        brand = manufacturer,
        manufacturer = manufacturer,
        quantity = quantity,
        images = listOf(imageUrl),
        isFavorite = isFavorite,
    )

    init {
        require(images.isNotEmpty()) { "Images cannot be empty" }
        require(images.all { it.isNotBlank() }) { "All images must not be blank" }
        require(images.distinct().size == images.size) { "Images must be unique" }
        require(imageUrl.isNotBlank()) { "Primary image URL cannot be blank" }
        require(imageUrl in images) { "Primary image URL must be one of the provided images" }
        require(quantity >= 0) { "Quantity cannot be negative" }
        require(year > 0) { "Year must be greater than 0" }
        require(model.isNotBlank()) { "Model cannot be blank" }
        require(manufacturer.isNotBlank()) { "Manufacturer cannot be blank" }
        require(brand.isNotBlank()) { "Brand cannot be blank" }
    }
}
