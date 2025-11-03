package io.github.patrickvillarroel.wheel.vault.ui.screen

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

/**
 * Adds a map of items.
 *
 * @param items the data map
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
inline fun <K, V> LazyListScope.items(
    items: Map<K, V>,
    noinline key: ((item: Map.Entry<K, V>) -> Any)? = null,
    crossinline contentType: (item: Map.Entry<K, V>) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(item: Map.Entry<K, V>) -> Unit,
) {
    val entriesList = items.entries.toList()
    items(
        count = entriesList.size,
        key = if (key != null) { index: Int -> key(entriesList[index]) } else null,
        contentType = { index: Int -> contentType(entriesList[index]) },
    ) { index: Int ->
        itemContent(entriesList[index])
    }
}

/**
 * Adds a map of items where the content of an item is aware of its index.
 *
 * @param items the data map
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
inline fun <K, V> LazyListScope.itemsIndexed(
    items: Map<K, V>,
    noinline key: ((index: Int, item: Map.Entry<K, V>) -> Any)? = null,
    crossinline contentType: (index: Int, item: Map.Entry<K, V>) -> Any? = { _, _ -> null },
    crossinline itemContent: @Composable LazyItemScope.(index: Int, item: Map.Entry<K, V>) -> Unit,
) {
    val entriesList = items.entries.toList()
    items(
        count = items.size,
        key = if (key != null) { index: Int -> key(index, entriesList[index]) } else null,
        contentType = { index: Int -> contentType(index, entriesList[index]) },
    ) { index: Int ->
        itemContent(index, entriesList[index])
    }
}
