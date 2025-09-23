package io.github.patrickvillarroel.wheel.vault.ui.screen.component

import io.github.patrickvillarroel.wheel.vault.util.CallbackInterceptor

open class HeaderMenuDropdownCallbacks(
    val onGarageClick: () -> Unit,
    val onFavoritesClick: () -> Unit,
    val onStatisticsClick: () -> Unit,
    val onExchangesClick: () -> Unit,
) {
    protected constructor(copy: HeaderMenuDropdownCallbacks) : this(
        onGarageClick = copy.onGarageClick,
        onFavoritesClick = copy.onFavoritesClick,
        onStatisticsClick = copy.onStatisticsClick,
        onExchangesClick = copy.onExchangesClick,
    )
    companion object {
        @JvmField
        val default = HeaderMenuDropdownCallbacks(
            onGarageClick = {},
            onFavoritesClick = {},
            onStatisticsClick = {},
            onExchangesClick = {},
        )
    }
}

open class HeaderCallbacks private constructor(
    val onProfileClick: () -> Unit,
    dropdownCallbacks: HeaderMenuDropdownCallbacks,
) : HeaderMenuDropdownCallbacks(dropdownCallbacks) {
    constructor(
        onProfileClick: () -> Unit,
        onGarageClick: () -> Unit,
        onFavoritesClick: () -> Unit,
        onStatisticsClick: () -> Unit,
        onExchangesClick: () -> Unit,
    ) : this(
        onProfileClick,
        HeaderMenuDropdownCallbacks(onGarageClick, onFavoritesClick, onStatisticsClick, onExchangesClick),
    )

    protected constructor(copy: HeaderCallbacks) : this(
        onProfileClick = copy.onProfileClick,
        onGarageClick = copy.onGarageClick,
        onFavoritesClick = copy.onFavoritesClick,
        onStatisticsClick = copy.onStatisticsClick,
        onExchangesClick = copy.onExchangesClick,
    )

    companion object {
        @JvmField
        val default = HeaderCallbacks(
            onProfileClick = {},
            dropdownCallbacks = HeaderMenuDropdownCallbacks.default,
        )
    }
}

open class HeaderBackCallbacks private constructor(val onBackClick: () -> Unit, headerCallbacks: HeaderCallbacks) :
    HeaderCallbacks(headerCallbacks) {
    constructor(
        onBackClick: () -> Unit,
        onProfileClick: () -> Unit,
        onGarageClick: () -> Unit,
        onFavoritesClick: () -> Unit,
        onStatisticsClick: () -> Unit,
        onExchangesClick: () -> Unit,
    ) : this(
        onBackClick,
        HeaderCallbacks(onProfileClick, onGarageClick, onFavoritesClick, onStatisticsClick, onExchangesClick),
    )
    companion object {
        @JvmField
        val default = HeaderBackCallbacks(
            onBackClick = {},
            headerCallbacks = HeaderCallbacks.default,
        )
    }
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
    onExchangesClick = {
        val callback = original.onExchangesClick
        val name = "onExchangesClick"
        specificInterceptors[name]?.intercept(name, callback) ?: interceptor.intercept(name, callback)
    },
)
