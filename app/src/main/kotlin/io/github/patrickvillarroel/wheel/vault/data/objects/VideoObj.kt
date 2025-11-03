package io.github.patrickvillarroel.wheel.vault.data.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
class VideoObj(
    val id: Uuid,
    val name: String,
    val description: String?,
    val link: String,
    @SerialName("created_at") val createdAt: Instant? = null,
)
