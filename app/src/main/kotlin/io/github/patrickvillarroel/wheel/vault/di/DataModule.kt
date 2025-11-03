package io.github.patrickvillarroel.wheel.vault.di

import androidx.room.Room
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.github.patrickvillarroel.wheel.vault.data.BrandRepositoryImpl
import io.github.patrickvillarroel.wheel.vault.data.CarRepositoryImpl
import io.github.patrickvillarroel.wheel.vault.data.GetVideosNewsUseCaseImpl
import io.github.patrickvillarroel.wheel.vault.data.UpdateOnboardingStateUseCaseImpl
import io.github.patrickvillarroel.wheel.vault.data.UpdateOnboardingStateUseCaseImpl.Companion.dataStore
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.CacheImageDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageDownloadHelper
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.ImageRepository
import io.github.patrickvillarroel.wheel.vault.data.datasource.image.MediaStoreImageDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.AppDatabase
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.BrandRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.CarRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.NewsRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.BrandSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.CarSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.TradeSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.TradeRepository
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import io.github.patrickvillarroel.wheel.vault.domain.usecase.UpdateOnboardingStateUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.BodyProgress
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.addDefaultResponseValidation
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

val dataModule = module {
    factory {
        HttpClient(CIO) {
            install(BodyProgress)
            install(ContentNegotiation)
            install(HttpTimeout) {
                requestTimeoutMillis = 60.seconds.inWholeMilliseconds
            }
            install(HttpRequestRetry) {
                maxRetries = 3
                exponentialDelay()
            }
            addDefaultResponseValidation()
            expectSuccess = true
        }
    }

    factory { CacheImageDataSource(androidContext()) }
    factory { MediaStoreImageDataSource(androidContext()) }
    factory { ImageRepository(get(), get()) }

    factory { ImageDownloadHelper(get(), get<SupabaseClient>().storage) }

    single<AppDatabase> {
        Room.databaseBuilder<AppDatabase>(androidContext(), "wheel_vault")
            .fallbackToDestructiveMigration(true)
            .build()
    }
    single { androidContext().dataStore }

    factory { BrandRoomDataSource(get<AppDatabase>().brandDao(), get()) }
    factory { CarRoomDataSource(get<AppDatabase>().carDao(), get()) }
    factory { NewsRoomDataSource(get<AppDatabase>().newsDao()) }
    factory { BrandSupabaseDataSource(get(), androidContext()) }
    factory<BrandRepository> { BrandRepositoryImpl(get(), get(), get()) }

    factory { CarSupabaseDataSource(get(), androidContext()) }
    factory<CarsRepository> { CarRepositoryImpl(get()) }

    factory<TradeRepository> { TradeSupabaseDataSource(get(), get()) }

    factory<UpdateOnboardingStateUseCase> { UpdateOnboardingStateUseCaseImpl(get()) }
    factory<GetVideosNewsUseCase> { GetVideosNewsUseCaseImpl(get(), get()) }
}
