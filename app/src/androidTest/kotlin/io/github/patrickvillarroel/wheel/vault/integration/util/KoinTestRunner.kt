package io.github.patrickvillarroel.wheel.vault.integration.util

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Custom test runner for instrumented tests with Koin.
 * This runner uses a custom Application class to initialize Koin with test modules.
 */
class KoinTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application =
        super.newApplication(cl, KoinTestApplication::class.java.name, context)
}
