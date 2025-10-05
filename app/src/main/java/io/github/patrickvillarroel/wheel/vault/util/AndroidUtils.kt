package io.github.patrickvillarroel.wheel.vault.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.annotation.IntRange
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream

/**
 * Convierte una URI de imagen en un ByteArray.
 * @param context Contexto de la aplicación.
 * @param uri URI de la imagen.
 * @return ByteArray con la imagen o null en caso de error. En caso de error se loggea el error.
 */
fun uriToByteArray(context: Context, uri: Uri): ByteArray? = try {
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        val bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap?.let { bitmapToByteArray(bitmap) } ?: inputStream.readBytes()
    }
} catch (e: Exception) {
    Log.e("ImageUtil", "Error reading uri to bytes", e)
    null
}

/** Calidad de compresión (0–100). Recomendado 80–85 para WEBP. */
@IntRange(from = 0, to = 100)
private const val COMPRESS_QUALITY = 85

/** Formato de compresión (JPEG, PNG, WEBP). */
private val COMPRESS_FORMAT = Bitmap.CompressFormat.WEBP

/**
 * Comprime un Bitmap en el formato indicado y lo devuelve como ByteArray.
 *
 * @param bitmap El bitmap original a comprimir.
 * @return ByteArray con la imagen comprimida. Bitmap es reciclado después de la compresión.
 */
fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val baos = ByteArrayOutputStream()
    bitmap.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, baos)
    bitmap.recycle()
    return baos.toByteArray()
}

/**
 * Rota un Bitmap según los grados indicados.
 * @param bitmap El bitmap a rotar.
 * @param rotationDegrees Grados de rotación.
 * @return El bitmap rotado. El bitmap original es reciclado.
 */
fun rotateBitmapIfNeeded(bitmap: Bitmap, rotationDegrees: Int): Bitmap = if (rotationDegrees != 0) {
    val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).also {
        if (bitmap != it) bitmap.recycle()
    }
} else {
    bitmap
}

private const val MAX_DIMENSION = 1080f

/**
 * Redimensiona un Bitmap para que sus dimensiones no excedan un valor máximo.
 * @param bitmap El bitmap a redimensionar.
 * @return El bitmap redimensionado. El bitmap original es reciclado.
 */
fun resizeBitmapMaxDimension(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val scale = if (width > height) MAX_DIMENSION / width else MAX_DIMENSION / height
    if (scale >= 1f) return bitmap
    val newWidth = (width * scale)
    val newHeight = (height * scale)
    return bitmap.scale(newWidth.toInt(), newHeight.toInt()).also {
        if (bitmap != it) bitmap.recycle()
    }
}
