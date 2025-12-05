package io.github.patrickvillarroel.wheel.vault.integration.util

import android.app.Application
import io.github.patrickvillarroel.wheel.vault.integration.di.testIntegrationModule
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE
import org.koin.dsl.KoinConfiguration
import org.koin.core.logger.Logger as KoinLogger

/**
 * Custom Application class for integration tests.
 * Initializes Koin with test modules instead of production modules.
 */
@OptIn(KoinExperimentalAPI::class)
class KoinTestApplication :
    Application(),
    KoinStartup {
    override fun onKoinStartup() = KoinConfiguration {
        logger(object : KoinLogger() {
            override fun display(level: Level, msg: MESSAGE) {
                if (level == Level.ERROR) {
                    println("[KOIN-TEST] $msg")
                }
            }
        })
        androidContext(this@KoinTestApplication)
        modules(testIntegrationModule)
    }
}
