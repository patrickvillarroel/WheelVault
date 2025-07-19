package io.github.patrickvillarroel.wheel.vault.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream

fun uriToByteArray(context: Context, uri: Uri): ByteArray? = try {
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        inputStream.readBytes()
    }
} catch (e: Exception) {
    Log.e("ImageUtil", "Error reading uri to bytes", e)
    null
}

fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
    return stream.toByteArray()
}
