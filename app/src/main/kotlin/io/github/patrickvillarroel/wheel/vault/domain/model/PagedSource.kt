package io.github.patrickvillarroel.wheel.vault.domain.model

/**
 * Paginated data source with a [Key] and [Value]
 *
 * This is an abstraction of [Paging3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)
 * library.
 *
 * @see PagedSource.loadPage
 * @see Page
 */
fun interface PagedSource<Key : Any, out Value : Any> {
    /**
     * Loads a page of data from the source.
     *
     * @param key The key to load the page from. If null, the first page is loaded.
     * @param size The maximum number of items to load in the page.
     * @return A [Page] containing the loaded data and the next key.
     */
    suspend fun loadPage(key: Key?, size: Int): Page<Key, Value>
}
