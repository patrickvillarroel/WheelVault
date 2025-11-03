package io.github.patrickvillarroel.wheel.vault.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

object SyncMediator {
    @JvmStatic
    suspend fun <T> fetch(
        forceRefresh: Boolean = false,
        localFetch: suspend () -> T?,
        remoteFetch: suspend () -> T?,
        saveRemote: suspend CoroutineScope.(data: T) -> Unit,
    ): T? {
        contract {
            callsInPlace(localFetch, InvocationKind.AT_MOST_ONCE)
            callsInPlace(remoteFetch, InvocationKind.AT_MOST_ONCE)
            callsInPlace(saveRemote, InvocationKind.AT_MOST_ONCE)
        }
        return if (forceRefresh) {
            val remoteData = remoteFetch()
            if (remoteData != null) {
                coroutineScope {
                    saveRemote(remoteData)
                }
            }
            remoteData
        } else {
            localFetch() ?: run {
                val remoteData = remoteFetch()
                if (remoteData != null) {
                    coroutineScope {
                        saveRemote(remoteData)
                    }
                }
                remoteData
            }
        }
    }

    @JvmStatic
    suspend fun <T> fetchList(
        forceRefresh: Boolean = false,
        localFetch: suspend () -> List<T>,
        remoteFetch: suspend () -> List<T>,
        saveRemote: suspend CoroutineScope.(data: List<T>) -> Unit,
    ): List<T> {
        contract {
            callsInPlace(localFetch, InvocationKind.AT_MOST_ONCE)
            callsInPlace(remoteFetch, InvocationKind.AT_MOST_ONCE)
            callsInPlace(saveRemote, InvocationKind.AT_MOST_ONCE)
        }
        return if (forceRefresh) {
            val remoteData = remoteFetch()
            coroutineScope {
                saveRemote(remoteData)
            }
            remoteData
        } else {
            val localData = localFetch()
            localData.ifEmpty {
                val remoteData = remoteFetch()
                if (remoteData.isNotEmpty()) {
                    coroutineScope {
                        saveRemote(remoteData)
                    }
                }
                remoteData
            }
        }
    }

    @JvmStatic
    suspend fun <K, V> fetchMap(
        forceRefresh: Boolean = false,
        localFetch: suspend () -> Map<K, V>,
        remoteFetch: suspend () -> Map<K, V>,
        saveRemote: suspend CoroutineScope.(data: Map<K, V>) -> Unit,
    ): Map<K, V> {
        contract {
            callsInPlace(localFetch, InvocationKind.AT_MOST_ONCE)
            callsInPlace(remoteFetch, InvocationKind.AT_MOST_ONCE)
            callsInPlace(saveRemote, InvocationKind.AT_MOST_ONCE)
        }
        return if (forceRefresh) {
            val remoteData = remoteFetch()
            coroutineScope {
                saveRemote(remoteData)
            }
            remoteData
        } else {
            val localData = localFetch()
            localData.ifEmpty {
                val remoteData = remoteFetch()
                if (remoteData.isNotEmpty()) {
                    coroutineScope {
                        saveRemote(remoteData)
                    }
                }
                remoteData
            }
        }
    }
}
