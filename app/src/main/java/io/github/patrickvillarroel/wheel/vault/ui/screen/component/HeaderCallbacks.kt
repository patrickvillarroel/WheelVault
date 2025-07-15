package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import io.github.patrickvillarroel.wheel.vault.util.CallbackInterceptor

open class HeaderMenuDropdownCallbacks(
    val onGarageClick: () -> Unit,
    val onFavoritesClick: () -> Unit,
    val onStatisticsClick: () -> Unit,
)

open class HeaderCallbacks(val onProfileClick: () -> Unit, dropdownCallbacks: HeaderMenuDropdownCallbacks) :
    HeaderMenuDropdownCallbacks(
        dropdownCallbacks.onGarageClick,
        dropdownCallbacks.onFavoritesClick,
        dropdownCallbacks.onStatisticsClick,
    ) {
    constructor(
        onProfileClick: () -> Unit,
        onGarageClick: () -> Unit,
        onFavoritesClick: () -> Unit,
        onStatisticsClick: () -> Unit,
    ) : this(onProfileClick, HeaderMenuDropdownCallbacks(onGarageClick, onFavoritesClick, onStatisticsClick))
}

open class HeaderBackCallbacks(val onBackClick: () -> Unit, headerCallbacks: HeaderCallbacks) :
    HeaderCallbacks(
        headerCallbacks.onProfileClick,
        headerCallbacks.onGarageClick,
        headerCallbacks.onFavoritesClick,
        headerCallbacks.onStatisticsClick,
    ) {
    constructor(
        onBackClick: () -> Unit,
        onProfileClick: () -> Unit,
        onGarageClick: () -> Unit,
        onFavoritesClick: () -> Unit,
        onStatisticsClick: () -> Unit,
    ) : this(onBackClick, HeaderCallbacks(onProfileClick, onGarageClick, onFavoritesClick, onStatisticsClick))
}

/**
 * Special callback to intercept header callbacks.
 * Thinking to use in [io.github.patrickvillarroel.wheel.vault.ui.screen.detail.car.edit.CarEditContent] or testing.
 */
class InterceptedHeaderBackCallbacks(
    private val original: HeaderBackCallbacks,
    private val interceptor: CallbackInterceptor,
    private val specificInterceptors: Map<String, CallbackInterceptor> = emptyMap(),
) : HeaderBackCallbacks(
    onBackClick = {
        val callback = original.onBackClick
        val name = "onBackClick"
        specificInterceptors[name]?.intercept(name, callback) ?: interceptor.intercept(name, callback)
    },
    onProfileClick = {
        val callback = original.onProfileClick
        val name = "onProfileClick"
        specificInterceptors[name]?.intercept(name, callback) ?: interceptor.intercept(name, callback)
    },
    onGarageClick = {
        val callback = original.onGarageClick
        val name = "onGarageClick"
        specificInterceptors[name]?.intercept(name, callback) ?: interceptor.intercept(name, callback)
    },
    onFavoritesClick = {
        val callback = original.onFavoritesClick
        val name = "onFavoritesClick"
        specificInterceptors[name]?.intercept(name, callback) ?: interceptor.intercept(name, callback)
    },
    onStatisticsClick = {
        val callback = original.onStatisticsClick
        val name = "onStatisticsClick"
        specificInterceptors[name]?.intercept(name, callback) ?: interceptor.intercept(name, callback)
    },
)
