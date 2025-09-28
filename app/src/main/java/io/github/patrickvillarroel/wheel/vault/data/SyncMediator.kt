@file:OptIn(ExperimentalContracts::class)

package io.github.patrickvillarroel.wheel.vault.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

object SyncMediator {
    suspend inline fun <T : Any> fetch(
        localFetch: suspend () -> T?,
        remoteFetch: suspend () -> T?,
        saveRemote: suspend (T?) -> Unit,
        forceRefresh: Boolean,
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

    suspend inline fun <T : Any> fetchList(
        localFetch: suspend () -> List<T>,
        remoteFetch: suspend () -> List<T>,
        crossinline saveRemote: suspend CoroutineScope.(List<T>) -> Unit,
        forceRefresh: Boolean = false,
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
