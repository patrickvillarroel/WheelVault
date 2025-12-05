package io.github.patrickvillarroel.wheel.vault.integration.di

import androidx.room.Room
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.PropertyConversionMethod
import io.github.jan.supabase.storage.Storage
import io.github.patrickvillarroel.wheel.vault.data.BrandRepositoryImpl
import io.github.patrickvillarroel.wheel.vault.data.CarRepositoryImpl
import io.github.patrickvillarroel.wheel.vault.data.GetVideosNewsUseCaseImpl
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
import io.github.patrickvillarroel.wheel.vault.data.mediator.CarOfflineFirstMediator
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.TradeRepository
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.ExchangeViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Test module for integration tests.
 * Uses in-memory Room database and real Supabase client with fake URL (won't make network calls).
 */
val testIntegrationModule = module {
    // In-memory Room Database for testing
    single<AppDatabase> {
        Room.inMemoryDatabaseBuilder<AppDatabase>(
            androidContext(),
        ).allowMainThreadQueries() // For testing purposes
            .build()
    }

    // DAOs from in-memory database
    single { get<AppDatabase>().brandDao() }
    single { get<AppDatabase>().carDao() }
    single { get<AppDatabase>().carImageDao() }
    single { get<AppDatabase>().newsDao() }

    // Real SupabaseClient with test credentials (won't make network calls because datasources are mocked)
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = "https://test.supabase.co",
            supabaseKey = "test-anon-key",
        ) {
            install(Auth)
            install(Postgrest) {
                propertyConversionMethod = PropertyConversionMethod.SERIAL_NAME
            }
            install(Storage)
        }
    }

    // Room Data Sources (real implementations with in-memory DB)
    factory { BrandRoomDataSource(get(), get()) }
    factory { CarRoomDataSource(get(), get()) }
    factory { GetVideoNewsRoomDataSource(get(), get()) }

    // Mocked Supabase Data Sources - relaxed mocks that return default values
    // Tests can configure specific behaviors as needed
    factory<BrandSupabaseDataSource> { mockk(relaxed = true) }
    factory<CarSupabaseDataSource> { mockk(relaxed = true) }
    factory<GetVideoNewsSupabaseDataSource> { mockk(relaxed = true) }
    factory<ImageDownloadHelper> { mockk(relaxed = true) }

    // Image management (mocked for tests since not critical for integration tests)
    factory {
        mockk<CacheImageDataSource>(relaxed = true) {
            coEvery { loadImage(any()) } returns null
            coEvery { saveImage(any(), any()) } returns Unit
            coEvery { deleteImage(any()) } returns true
        }
    }

    // Mediators
    factory { CarOfflineFirstMediator(get<CarSupabaseDataSource>()) }

    // Repositories (real implementations with mocked Supabase)
    factory<BrandRepository> {
        BrandRepositoryImpl(
            get<BrandSupabaseDataSource>(),
            get<BrandRoomDataSource>(),
            get(),
            get(),
        )
    }
    factory<CarsRepository> {
        CarRepositoryImpl(
            get<CarRoomDataSource>(),
            get<CarSupabaseDataSource>(),
            get(), // CarDao
        )
    }

    // TradeSupabaseDataSource and TradeRepository - fully mocked since it only uses Supabase
    // Use single instead of factory so the same mock instance is used everywhere
    single<TradeSupabaseDataSource> {
        mockk(relaxed = true)
    }
    single<TradeRepository> { get<TradeSupabaseDataSource>() }

    // Use Cases
    factory<GetVideosNewsUseCase> { GetVideosNewsUseCaseImpl(get(), get(), get()) }

    // ViewModels
    viewModel { BrandViewModel(get(), get()) }
    viewModel { CarViewModel(get()) }
    viewModel { GarageViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    single { ExchangeViewModel(get(), get()) }
}
