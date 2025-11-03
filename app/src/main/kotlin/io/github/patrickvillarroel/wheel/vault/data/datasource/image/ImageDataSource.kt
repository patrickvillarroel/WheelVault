package io.github.patrickvillarroel.wheel.vault.data.datasource.image

interface ImageDataSource {
    suspend fun saveImage(name: String, bytes: ByteArray)
    suspend fun loadImage(name: String): ByteArray?
    suspend fun deleteImage(name: String): Boolean
}
