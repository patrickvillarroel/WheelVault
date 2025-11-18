package io.github.patrickvillarroel.wheel.vault.di

import androidx.room.Room
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.github.patrickvillarroel.wheel.vault.BuildConfig
import io.github.patrickvillarroel.wheel.vault.data.CarRepositoryImpl
import io.github.patrickvillarroel.wheel.vault.data.GetVideosNewsUseCaseImpl
import io.github.patrickvillarroel.wheel.vault.data.UpdateOnboardingStateUseCaseImpl
import io.github.patrickvillarroel.wheel.vault.data.UpdateOnboardingStateUseCaseImpl.Companion.dataStore
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.CacheImageDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageDownloadHelper
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.AppDatabase
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.BrandRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.CarRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.GetVideoNewsRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.BrandSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.CarSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.GetVideoNewsSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.TradeSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.TradeRepository
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import io.github.patrickvillarroel.wheel.vault.domain.usecase.UpdateOnboardingStateUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.BodyProgress
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.addDefaultResponseValidation
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingFormat
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.dsl.onClose
import kotlin.time.Duration.Companion.seconds
import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.plugins.logging.Logger as KtorLogger

val dataModule = module {
    single {
        HttpClient(CIO) {
            install(BodyProgress)
            install(HttpRedirect)
            install(ContentNegotiation)
            install(HttpTimeout) {
                requestTimeoutMillis = 60.seconds.inWholeMilliseconds
            }
            install(HttpRequestRetry) {
                maxRetries = 3
                exponentialDelay()
            }
            install(HttpCache) {
                publicStorage(FileStorage(androidContext().cacheDir.resolve("ktor_public_cache")))
                privateStorage(FileStorage(androidContext().cacheDir.resolve("ktor_private_cache")))
            }
            if (BuildConfig.DEBUG) {
                install(Logging) {
                    level = LogLevel.INFO
                    format = LoggingFormat.OkHttp
                    logger = object : KtorLogger {
                        override fun log(message: String) {
                            // verbose level
                            KermitLogger.v("HttpClient") { message }
                        }
                    }
                }
            }
            addDefaultResponseValidation()
            expectSuccess = true
        }
    } onClose { httpClient -> httpClient?.close() }

    // TODO can be a good o bad idea use coil3 disk cache as local data source of images
    factory { CacheImageDataSource(androidContext()) }
    // factory { MediaStoreImageDataSource(androidContext()) }
    // factory { ImageRepository(get(), get()) }

    factory { ImageDownloadHelper(get(), get<SupabaseClient>().storage) }

    single<AppDatabase> {
        Room.databaseBuilder<AppDatabase>(androidContext(), "wheel_vault")
            .fallbackToDestructiveMigration(true)
            .build()
    }
    single { androidContext().dataStore }

    factory { BrandRoomDataSource(get<AppDatabase>().brandDao(), get()) }
    factory { CarRoomDataSource(get<AppDatabase>().carDao(), get()) }
    // TODO change to impl when sync mediator and room is ready
    factory<BrandRepository> { BrandSupabaseDataSource(get(), androidContext()) }
    // factory<BrandRepository> { BrandRepositoryImpl(get(), get(), get(), get()) }

    factory { CarSupabaseDataSource(get(), androidContext()) }
    factory<CarsRepository> { CarRepositoryImpl(get()) }

    factory<TradeRepository> { TradeSupabaseDataSource(get(), get()) }

    factory<UpdateOnboardingStateUseCase> { UpdateOnboardingStateUseCaseImpl(get()) }

    factory { GetVideoNewsSupabaseDataSource(get(), androidContext()) }
    factory { GetVideoNewsRoomDataSource(get<AppDatabase>().newsDao(), get()) }
    factory<GetVideosNewsUseCase> { GetVideosNewsUseCaseImpl(get(), get(), get()) }
}
