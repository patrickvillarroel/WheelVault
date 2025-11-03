package io.github.patrickvillarroel.wheel.vault.data.datasource.image

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import android.content.Context as AndroidContext

class CacheImageDataSource(context: AndroidContext) : ImageDataSource {
    private val cacheDir: File = context.cacheDir

    override suspend fun saveImage(name: String, bytes: ByteArray) {
        withContext(Dispatchers.IO) {
            logger.v { "Saving image '$name' into cache directory with size ${bytes.size}" }
            val file = File(cacheDir, name)
            if (file.exists()) {
                logger.w { "File '$name' already exists, deleting it to replace" }
                file.delete()
            }
            file.outputStream().use { it.write(bytes) }
        }
    }

    override suspend fun loadImage(name: String): ByteArray? = withContext(Dispatchers.IO) {
        logger.v { "Loading image '$name' from cache directory" }
        val file = File(cacheDir, name)
        if (file.exists()) {
            logger.v { "File '$name' exists, retrieving it" }
            file.readBytes().also {
                logger.v { "File '$name' retrieved with size ${it.size}" }
            }
        } else {
            logger.v { "File '$name' does not exist in cache" }
            null
        }
    }

    override suspend fun deleteImage(name: String): Boolean = withContext(Dispatchers.IO) {
        logger.v { "Deleting image '$name' from cache directory" }
        val file = File(cacheDir, name)
        file.delete().also { deleted -> logger.v { "File '$name' deleted $deleted" } }
    }

    companion object {
        private val logger = Logger.withTag("CacheImageDataSource")
    }
}
