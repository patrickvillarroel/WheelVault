package io.github.patrickvillarroel.wheel.vault.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.IOException

fun uriToByteArray(context: Context, uri: Uri): ByteArray {
    val inputStream = context.contentResolver.openInputStream(uri)
    return inputStream?.readBytes() ?: throw IOException("No se pudo leer el archivo")
}

fun createTempFileForImage(context: Context): File {
    val tempDir = File(context.cacheDir, "images_preview")
    tempDir.mkdirs()
    return File.createTempFile("captured_", ".jpg", tempDir)
}
