package io.github.patrickvillarroel.wheel.vault.data.datasource.image

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CacheImageDataSource(context: Context) : ImageDataSource {
    private val cacheDir: File = context.cacheDir

    override suspend fun saveImage(name: String, bytes: ByteArray): Uri? = withContext(Dispatchers.IO) {
        val file = File(cacheDir, name)
        if (file.exists()) file.delete()
        file.outputStream().use { it.write(bytes) }
        Uri.fromFile(file)
    }

    override suspend fun loadImage(name: String): ByteArray? = withContext(Dispatchers.IO) {
        val file = File(cacheDir, name)
        if (file.exists()) file.readBytes() else null
    }

    override suspend fun deleteImage(name: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(cacheDir, name)
        file.delete()
    }
}
