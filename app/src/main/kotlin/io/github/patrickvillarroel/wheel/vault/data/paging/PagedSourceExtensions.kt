package io.github.patrickvillarroel.wheel.vault.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.patrickvillarroel.wheel.vault.domain.model.PagedSource
import kotlin.coroutines.cancellation.CancellationException

/**
 * Converts a [PagedSource] to a [PagingSource].
 *
 * @receiver The [PagedSource] to convert.
 * @return A [PagingSource] that can be used with a [androidx.paging.Pager].
 * @see PagingSource
 */
fun <Key : Any, Value : Any> PagedSource<Key, Value>.asPagingSource(): PagingSource<Key, Value> =
    DomainPagingSource(this)

private class DomainPagingSource<Key : Any, Value : Any>(
    private val source: PagedSource<Key, Value>,
) : PagingSource<Key, Value>() {
    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, Value> = try {
        val page = source.loadPage(params.key, params.loadSize)
        LoadResult.Page(data = page.data, prevKey = page.prevKey, nextKey = page.nextKey)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Key, Value>): Key? = state.anchorPosition?.let { position ->
        val closest = state.closestPageToPosition(position)
        closest?.prevKey ?: closest?.nextKey
    }
}
