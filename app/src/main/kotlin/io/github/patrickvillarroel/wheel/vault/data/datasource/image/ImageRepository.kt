package io.github.patrickvillarroel.wheel.vault.data.datasource.image

import android.net.Uri

class ImageRepository(private val cache: CacheImageDataSource, private val mediaStore: MediaStoreImageDataSource) {

    suspend fun saveImage(name: String, bytes: ByteArray, persist: Boolean = false): Uri? {
        val cacheUri = cache.saveImage(name, bytes)
        return if (persist) mediaStore.saveImage(name, bytes) else cacheUri
    }

    suspend fun loadImage(name: String): ByteArray? = cache.loadImage(name) ?: mediaStore.loadImage(name)?.also {
        // refrescar cache para accesos rápidos posteriores
        cache.saveImage(name, it)
    }

    suspend fun deleteImage(name: String, persist: Boolean = false): Boolean {
        val cacheDelete = cache.deleteImage(name)
        return if (persist) mediaStore.deleteImage(name) else cacheDelete
    }
}
