package io.github.patrickvillarroel.wheel.vault.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// TODO can catch exceptions of remote fetch and fallback to local fetch maybe
object SyncMediator {
    private val logger = Logger.withTag("SyncMediator")

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
            logger.v { "Forced refresh triggered" }
            val remoteData = remoteFetch()
            if (remoteData != null) {
                logger.v { "Found data in remote was not null, saving in local store" }
                coroutineScope {
                    saveRemote(remoteData)
                }
            }
            remoteData
        } else {
            localFetch() ?: run {
                logger.v { "Local store don't have data, fallback to remote store" }
                val remoteData = remoteFetch()
                if (remoteData != null) {
                    logger.v { "Remote store have data was not null, saving in local store" }
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
            logger.v { "Forced refresh a list of data" }
            val remoteData = remoteFetch()
            if (remoteData.isNotEmpty()) {
                logger.v { "Remote store found data and is not empty list, saving in local store" }
                coroutineScope {
                    saveRemote(remoteData)
                }
            }
            remoteData
        } else {
            val localData = localFetch()
            localData.ifEmpty {
                logger.v { "Local store don't have data in list, fallback to remote store" }
                val remoteData = remoteFetch()
                if (remoteData.isNotEmpty()) {
                    logger.v { "Remote store found data and is not empty list, saving in local store" }
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
            logger.v { "Forced refresh data as map" }
            val remoteData = remoteFetch()
            if (remoteData.isNotEmpty()) {
                logger.v { "Remote store have data and is not empty map, saving in local store" }
                coroutineScope {
                    saveRemote(remoteData)
                }
            }
            remoteData
        } else {
            val localData = localFetch()
            localData.ifEmpty {
                logger.v { "Local store have empty map, fallback to remote store" }
                val remoteData = remoteFetch()
                if (remoteData.isNotEmpty()) {
                    logger.v { "Remote store found data and is not empty map, saving in local store" }
                    coroutineScope {
                        saveRemote(remoteData)
                    }
                }
                remoteData
            }
        }
    }
}
