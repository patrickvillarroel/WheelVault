package io.github.patrickvillarroel.wheel.vault

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.coil.coil3
import io.github.patrickvillarroel.wheel.vault.di.wheelVaultModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import java.io.File

@OptIn(KoinExperimentalAPI::class)
class MainApplication :
    Application(),
    KoinStartup,
    SingletonImageLoader.Factory {
    private val supabase: SupabaseClient by inject()

    override fun onKoinStartup() = KoinConfiguration {
        modules(wheelVaultModule)
        androidContext(this@MainApplication)
    }

    @OptIn(SupabaseExperimental::class)
    override fun newImageLoader(context: PlatformContext): ImageLoader = ImageLoader.Builder(context)
        .components {
            add(supabase.coil3)
            add(KtorNetworkFetcherFactory())
        }
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(File(context.cacheDir, "image_cache"))
                .maxSizeBytes(100L * 1024 * 1024)
                .build()
        }
        .build()
}
