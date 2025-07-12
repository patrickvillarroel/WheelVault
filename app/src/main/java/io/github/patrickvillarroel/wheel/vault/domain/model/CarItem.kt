package io.github.patrickvillarroel.wheel.vault.domain.model

import kotlin.random.Random

data class CarItem(
    // TODO
    val id: Int = Random.nextInt(),
    val name: String,
    val year: Int,
    val manufacturer: String,
    val quantity: Int,
    val imageUrl: String,
    val isFavorite: Boolean,
)
