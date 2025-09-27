package io.github.patrickvillarroel.wheel.vault.data.datasource.image

import android.net.Uri

interface ImageDataSource {
    suspend fun saveImage(name: String, bytes: ByteArray): Uri?
    suspend fun loadImage(name: String): ByteArray?
    suspend fun deleteImage(name: String): Boolean
}
