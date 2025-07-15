package io.github.patrickvillarroel.wheel.vault.util

fun interface CallbackInterceptor {
    fun intercept(actionName: String, action: () -> Unit)
}
