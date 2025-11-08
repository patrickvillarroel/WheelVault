package io.github.patrickvillarroel.wheel.vault.ui.screen.garage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import co.touchlab.kermit.Logger
import io.github.patrickvillarroel.wheel.vault.data.paging.asPagingSource
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

class GarageViewModel(private val carsRepository: CarsRepository) : ViewModel() {
    companion object {
        private val logger = Logger.withTag("GarageViewModel")
    }

    // Filter states
    private val searchQuery = MutableStateFlow<String?>(null)
    private val manufacturer = MutableStateFlow<String?>(null)
    private val isFavorite = MutableStateFlow(false)
    private val orderAsc = MutableStateFlow(false)

    // Combined filter state for triggering pagination refresh
    private data class FilterState(
        val query: String?,
        val manufacturer: String?,
        val isFavorite: Boolean,
        val orderAsc: Boolean,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val carsPaged = combine(
        searchQuery,
        manufacturer,
        isFavorite,
        orderAsc,
    ) { query, manufacturer, favorite, order ->
        FilterState(query, manufacturer, favorite, order)
    }.flatMapLatest { filters ->
        Pager(config = PagingConfig(pageSize = 10, initialLoadSize = 10)) {
            carsRepository.fetchPagedWithFilters(
                query = filters.query,
                manufacturer = filters.manufacturer,
                isFavorite = filters.isFavorite,
                orderAsc = filters.orderAsc,
            ).asPagingSource()
        }.flow
    }.cachedIn(viewModelScope)

    // Methods to update filter states - pagination will automatically refresh
    fun setSearchQuery(query: String?) {
        logger.d { "Setting search query: $query" }
        searchQuery.update { q -> query.takeIf { !it.isNullOrBlank() } ?: q }
    }

    fun setManufacturerFilter(manufacturer: String?) {
        logger.d { "Setting manufacturer filter: $manufacturer" }
        this.manufacturer.update { m -> manufacturer.takeIf { !it.isNullOrBlank() } ?: m }
    }

    fun setFavoriteFilter(isFavorite: Boolean) {
        logger.d { "Setting favorite filter: $isFavorite" }
        this.isFavorite.update { isFavorite }
    }

    fun setSortOrder(orderAsc: Boolean) {
        logger.d { "Setting sort order ascending: $orderAsc" }
        this.orderAsc.update { orderAsc }
    }

    fun clearFilters() {
        logger.d { "Clearing all filters" }
        searchQuery.value = null
        manufacturer.value = null
        isFavorite.value = false
        orderAsc.value = false
    }

    // Legacy methods for backward compatibility - now just delegate to filter setters
    fun fetchAll(force: Boolean = false, orderAsc: Boolean = false) {
        logger.d { "fetchAll called (legacy) - clearing filters and setting order" }
        searchQuery.value = null
        manufacturer.value = null
        isFavorite.value = false
        this.orderAsc.value = orderAsc
    }

    fun search(query: String, favoritesOnly: Boolean = false) {
        logger.d { "search called (legacy) - setting query and favorites" }
        searchQuery.update { q -> query.takeIf { it.isNotBlank() } ?: q }
        manufacturer.value = null
        isFavorite.value = favoritesOnly
    }

    fun fetchFavorites() {
        logger.d { "fetchFavorites called (legacy) - setting favorites filter" }
        searchQuery.value = null
        manufacturer.value = null
        isFavorite.value = true
    }

    fun filterByManufacturer(manufacturer: String) {
        logger.d { "filterByManufacturer called (legacy) - setting manufacturer filter" }
        searchQuery.value = null
        this.manufacturer.update { manufacturer }
    }
}
