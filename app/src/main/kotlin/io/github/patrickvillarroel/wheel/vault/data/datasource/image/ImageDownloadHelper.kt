package io.github.patrickvillarroel.wheel.vault.data.datasource.image

import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.StorageItem
import io.github.jan.supabase.storage.authenticatedRequest
import io.ktor.client.HttpClient
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class ImageDownloadHelper(private val httpClient: HttpClient, private val storage: Storage) {
    suspend fun downloadImage(item: StorageItem): ByteArray? {
        // 1. construir la URL autenticada
        val bucket = storage[item.bucketId]

        val (token, url) = if (item.authenticated) {
            bucket.authenticatedRequest(item.path)
        } else {
            null to bucket.publicUrl(item.path)
        }

        // 2. descargar los bytes
        return try {
            httpClient.get(url) {
                expectSuccess = true
                if (item.authenticated) header("Authorization", "Bearer $token")
            }.bodyAsBytes()
        } catch (_: Exception) {
            currentCoroutineContext().ensureActive()
            null
        }
    }
}
