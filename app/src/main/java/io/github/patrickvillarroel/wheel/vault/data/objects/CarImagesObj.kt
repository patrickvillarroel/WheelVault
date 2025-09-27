@file:OptIn(ExperimentalUuidApi::class)

package io.github.patrickvillarroel.wheel.vault.data.objects

import io.ktor.http.ContentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** The images of [CarObj] */
@Serializable
class CarImagesObj(
    @SerialName("car_id")
    val carId: Uuid,
    @SerialName("storage_path")
    val storagePath: String,
    @SerialName("mime_type")
    val mimeType: String,
    @SerialName("uploaded_by")
    val uploadedBy: Uuid,
    val id: Uuid? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("is_primary")
    val isPrimary: Boolean = false,
) {
    val contentType by lazy {
        ContentType.parse(mimeType)
    }

    companion object {
        const val TABLE = "car_images"
    }
}
