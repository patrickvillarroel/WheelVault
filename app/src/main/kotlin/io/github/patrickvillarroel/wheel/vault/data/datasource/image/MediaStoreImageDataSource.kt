package io.github.patrickvillarroel.wheel.vault.data.datasource.image

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Deprecated("This class is not tested, can produce errors")
class MediaStoreImageDataSource(private val context: Context) : ImageDataSource {
    override suspend fun saveImage(name: String, bytes: ByteArray): Uri? = withContext(Dispatchers.IO) {
        val values = ContentValues(2).apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            resolver.openOutputStream(it)?.use { stream ->
                stream.write(bytes)
            }
        }
        uri
    }

    override suspend fun loadImage(name: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            val resolver = context.contentResolver
            val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME)

            val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                "${MediaStore.Images.Media.DISPLAY_NAME}=?",
                arrayOf(name),
                null,
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    return@withContext resolver.openInputStream(uri)?.use { input -> input.readBytes() }
                }
            }
            null
        }
    }

    override suspend fun deleteImage(name: String): Boolean = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val where = "${MediaStore.Images.Media.DISPLAY_NAME}=?"
        val deleted = resolver.delete(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            where,
            arrayOf(name),
        )
        deleted > 0
    }
}
