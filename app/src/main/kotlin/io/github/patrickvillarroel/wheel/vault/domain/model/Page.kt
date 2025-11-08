package io.github.patrickvillarroel.wheel.vault.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * Represents a page of data with pagination keys.
 *
 * @param Key The type of the pagination key
 * @param Value The type of the data items
 * @param data The list of items in this page
 * @param prevKey The key for the previous page, or null if this is the first page
 * @param nextKey The key for the next page, or null if this is the last page
 */
@Immutable
@Stable
data class Page<out Key : Any, out Value : Any>(
    val data: List<Value>,
    val prevKey: Key?,
    val nextKey: Key?,
)
