package io.github.patrickvillarroel.wheel.vault

import android.app.Application
import io.github.patrickvillarroel.wheel.vault.di.wheelVaultModule
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

@OptIn(KoinExperimentalAPI::class)
class MainApplication :
    Application(),
    KoinStartup {

    override fun onKoinStartup() = KoinConfiguration {
        modules(wheelVaultModule)
        androidContext(this@MainApplication)
    }
}
