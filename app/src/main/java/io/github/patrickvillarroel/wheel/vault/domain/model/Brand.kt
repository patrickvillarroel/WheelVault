package io.github.patrickvillarroel.wheel.vault.domain.model

import java.util.UUID

data class Brand(
    val name: String,
    val description: String,
    val image: Any,
    val contentDescription: String?,
    val id: UUID = UUID.randomUUID(),
)
