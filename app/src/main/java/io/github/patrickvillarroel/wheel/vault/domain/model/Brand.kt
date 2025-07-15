package io.github.patrickvillarroel.wheel.vault.domain.model

import kotlin.random.Random

data class Brand(
    val name: String,
    val description: String,
    val image: Any,
    val contentDescription: String?,
    val id: Int = Random.nextInt(),
)
