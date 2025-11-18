package io.github.patrickvillarroel.wheel.vault.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import io.github.patrickvillarroel.wheel.vault.data.paging.asPagingSource
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onStart
import kotlin.time.Duration.Companion.seconds

class HomeViewModel(
    private val getVideosNewsUseCase: GetVideosNewsUseCase,
    private val brandRepository: BrandRepository,
    private val carsRepository: CarsRepository,
) : ViewModel() {
    val news = Pager(config = PagingConfig(pageSize = 10, initialLoadSize = 5)) {
        getVideosNewsUseCase.getVideosPaged().asPagingSource()
    }.flow.cachedIn(viewModelScope).onStart {
        delay(2.seconds)
    }

    val brandsImages = Pager(config = PagingConfig(pageSize = 10, initialLoadSize = 5)) {
        brandRepository.fetchAllImagesPaged().asPagingSource()
    }.flow.cachedIn(viewModelScope).onStart {
        delay(1.seconds)
    }

    val recentCarImages = Pager(config = PagingConfig(pageSize = 10, initialLoadSize = 5)) {
        carsRepository.fetchAllImagePaged(orderAsc = false).asPagingSource()
    }.flow.cachedIn(viewModelScope)
}
