package io.github.patrickvillarroel.wheel.vault.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

object SyncMediator {
    @JvmStatic
    suspend fun <T : Any> fetch(
        forceRefresh: Boolean = false,
        localFetch: suspend () -> T?,
        remoteFetch: suspend () -> T?,
        saveRemote: suspend (T?) -> Unit,
    ): T? {
        contract {
            callsInPlace(localFetch, InvocationKind.AT_MOST_ONCE)
            callsInPlace(remoteFetch, InvocationKind.AT_MOST_ONCE)
            callsInPlace(saveRemote, InvocationKind.AT_MOST_ONCE)
        }
        return if (forceRefresh) {
            val remoteData = remoteFetch()
            saveRemote(remoteData)
            remoteData
        } else {
            localFetch() ?: run {
                val remoteData = remoteFetch()
                saveRemote(remoteData)
                remoteData
            }
        }
    }

    @JvmStatic
    suspend fun <T : Any> fetchList(
        forceRefresh: Boolean = false,
        localFetch: suspend () -> List<T>,
        remoteFetch: suspend () -> List<T>,
        saveRemote: suspend CoroutineScope.(List<T>) -> Unit,
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
                coroutineScope {
                    saveRemote(remoteData)
                }
                remoteData
            }
        }
    }
}
