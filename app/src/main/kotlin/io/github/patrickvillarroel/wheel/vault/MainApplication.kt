package io.github.patrickvillarroel.wheel.vault

import android.app.Application
import co.touchlab.kermit.Severity
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.cachecontrol.CacheControlCacheStrategy
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.coil.coil3
import io.github.patrickvillarroel.wheel.vault.di.wheelVaultModule
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.logger.MESSAGE
import org.koin.dsl.KoinConfiguration
import co.touchlab.kermit.Logger as KermitLogger
import coil3.util.Logger as Coil3Logger
import org.koin.android.ext.android.get as koinGet
import org.koin.core.logger.Level as KoinLoggerLevel
import org.koin.core.logger.Logger as KoinLogger

/** Wheel Vault app */
@OptIn(KoinExperimentalAPI::class)
class MainApplication :
    Application(),
    KoinStartup,
    SingletonImageLoader.Factory {

    init {
        KermitLogger.mutableConfig.minSeverity = if (BuildConfig.DEBUG) Severity.Verbose else Severity.Info
    }

    override fun onKoinStartup() = KoinConfiguration {
        // Log Koin into Android logger
        logger(
            if (BuildConfig.DEBUG) {
                object : KoinLogger() {
                    private val logger = KermitLogger.withTag("koin")

                    override fun display(level: KoinLoggerLevel, msg: MESSAGE) {
                        when (level) {
                            KoinLoggerLevel.DEBUG -> logger.d(msg)
                            KoinLoggerLevel.INFO -> logger.i(msg)
                            KoinLoggerLevel.WARNING -> logger.w(msg)
                            KoinLoggerLevel.ERROR -> logger.e(msg)
                            KoinLoggerLevel.NONE -> {
                                // do nothing
                            }
                        }
                    }
                }
            } else {
                object : KoinLogger() {
                    override fun display(level: KoinLoggerLevel, msg: MESSAGE) {
                        // No Op
                    }
                }
            },
        )
        // Reference Android context
        androidContext(this@MainApplication)
        // Load modules
        modules(wheelVaultModule)
    }

    @OptIn(SupabaseExperimental::class, ExperimentalCoilApi::class)
    override fun newImageLoader(context: PlatformContext): ImageLoader = ImageLoader.Builder(context)
        .components {
            add(koinGet<SupabaseClient>().coil3)
            add(
                KtorNetworkFetcherFactory(
                    httpClient = { koinGet<HttpClient>() },
                    cacheStrategy = { CacheControlCacheStrategy() },
                ),
            )
        }
        .logger(
            if (BuildConfig.DEBUG) {
                object : Coil3Logger {
                    private val logger = KermitLogger.withTag("Coil3")

                    override var minLevel: Coil3Logger.Level
                        get() = when (logger.mutableConfig.minSeverity) {
                            Severity.Verbose -> Coil3Logger.Level.Verbose
                            Severity.Debug -> Coil3Logger.Level.Debug
                            Severity.Info -> Coil3Logger.Level.Info
                            Severity.Warn -> Coil3Logger.Level.Warn
                            Severity.Error -> Coil3Logger.Level.Error
                            Severity.Assert -> Coil3Logger.Level.Error
                        }
                        set(value) {
                            when (value) {
                                Coil3Logger.Level.Verbose -> logger.mutableConfig.minSeverity = Severity.Verbose
                                Coil3Logger.Level.Debug -> logger.mutableConfig.minSeverity = Severity.Debug
                                Coil3Logger.Level.Info -> logger.mutableConfig.minSeverity = Severity.Info
                                Coil3Logger.Level.Warn -> logger.mutableConfig.minSeverity = Severity.Warn
                                Coil3Logger.Level.Error -> logger.mutableConfig.minSeverity = Severity.Error
                            }
                        }

                    override fun log(tag: String, level: Coil3Logger.Level, message: String?, throwable: Throwable?) {
                        when (level) {
                            Coil3Logger.Level.Verbose -> logger.v(message ?: "", throwable, tag)
                            Coil3Logger.Level.Debug -> logger.d(message ?: "", throwable, tag)
                            Coil3Logger.Level.Info -> logger.i(message ?: "", throwable, tag)
                            Coil3Logger.Level.Warn -> logger.w(message ?: "", throwable, tag)
                            Coil3Logger.Level.Error -> logger.e(message ?: "", throwable, tag)
                        }
                    }
                }
            } else {
                null
            },
        )
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizeBytes(100L * 1024 * 1024)
                .build()
        }
        .build()
}
