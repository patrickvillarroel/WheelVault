package io.github.patrickvillarroel.wheel.vault.ui.util

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

/**
 * Adds a list of paging items.
 * @param pagingItems A [LazyPagingItems] object
 * @param key a factory of stable and unique keys representing the item. Using the same key for
 *   multiple items in the list is not allowed. Type of the key should be saveable via Bundle on
 *   Android. If null is passed the position in the list will represent the key. When you specify
 *   the key the scroll position will be maintained based on the key, which means if you add/remove
 *   items before the current visible item the item with the given key will be kept as the first
 *   visible one. This can be overridden by calling 'requestScrollToItem' on the 'LazyListState'.
 * @param contentType a factory of the content types for the item. The item compositions of the same
 *   type could be reused more efficiently. Note that null is a valid type and items of such type
 *   will be considered compatible.
 * @param itemContent the content displayed by a single item
 */
inline fun <T : Any> LazyListScope.items(
    pagingItems: LazyPagingItems<out T>,
    noinline key: ((item: @JvmSuppressWildcards T) -> Any)? = null,
    noinline contentType: (item: @JvmSuppressWildcards T) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(item: @JvmSuppressWildcards T) -> Unit,
) = items(
    count = pagingItems.itemCount,
    key = pagingItems.itemKey(key),
    contentType = pagingItems.itemContentType(contentType),
) { index: Int ->
    pagingItems[index]?.let { item -> itemContent(item) }
}

/**
 * Adds a list of paging items where the content of an item is aware of its index.
 * @param pagingItems A [LazyPagingItems] object
 * @param key a factory of stable and unique keys representing the item. Using the same key for
 *   multiple items in the list is not allowed. Type of the key should be saveable via Bundle on
 *   Android. If null is passed the position in the list will represent the key. When you specify
 *   the key the scroll position will be maintained based on the key, which means if you add/remove
 *   items before the current visible item the item with the given key will be kept as the first
 *   visible one. This can be overridden by calling 'requestScrollToItem' on the 'LazyListState'.
 * @param contentType a factory of the content types for the item. The item compositions of the same
 *   type could be reused more efficiently. Note that null is a valid type and items of such type
 *   will be considered compatible.
 * @param itemContent the content displayed by a single item
 */
inline fun <T : Any> LazyListScope.itemsIndexed(
    pagingItems: LazyPagingItems<out T>,
    noinline key: ((index: Int, item: T) -> Any)? = null,
    crossinline contentType: (index: Int, item: T) -> Any? = { _, _ -> null },
    crossinline itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit,
) = items(
    count = pagingItems.itemCount,
    key = { index: Int ->
        pagingItems.itemKey(
            key = key?.let { keyNotNull ->
                { item -> keyNotNull(pagingItems.itemSnapshotList.items.indexOf(item), item) }
            },
        )(index)
    },
    contentType = { index: Int ->
        pagingItems.itemContentType { item ->
            contentType(pagingItems.itemSnapshotList.items.indexOf(item), item)
        }(index)
    },
) { index: Int ->
    pagingItems[index]?.let { item -> itemContent(index, item) }
}
