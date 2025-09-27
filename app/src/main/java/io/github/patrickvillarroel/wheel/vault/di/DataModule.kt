package io.github.patrickvillarroel.wheel.vault.di

import androidx.room.Room
import io.github.patrickvillarroel.wheel.vault.data.BrandRepositoryImpl
import io.github.patrickvillarroel.wheel.vault.data.CarRepositoryImpl
import io.github.patrickvillarroel.wheel.vault.data.GetVideosNewsUseCaseImpl
import io.github.patrickvillarroel.wheel.vault.data.UpdateOnboardingStateUseCaseImpl
import io.github.patrickvillarroel.wheel.vault.data.UpdateOnboardingStateUseCaseImpl.Companion.dataStore
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.AppDatabase
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.BrandRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.room.CarRoomDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.BrandSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.CarSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import io.github.patrickvillarroel.wheel.vault.domain.usecase.UpdateOnboardingStateUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<AppDatabase> {
        Room.databaseBuilder<AppDatabase>(androidContext(), "wheel_vault")
            .fallbackToDestructiveMigration(true)
            .build()
    }
    single { androidContext().dataStore }

    single { BrandRoomDataSource(get<AppDatabase>().brandDao()) }
    single { CarRoomDataSource(get<AppDatabase>().carDao()) }

    single { BrandSupabaseDataSource(get(), get()) }
    single<BrandRepository> { BrandRepositoryImpl(get()) }

    single { CarSupabaseDataSource(get(), get()) }
    single<CarsRepository> { CarRepositoryImpl(get()) }

    single<UpdateOnboardingStateUseCase> { UpdateOnboardingStateUseCaseImpl(get()) }
    single<GetVideosNewsUseCase> { GetVideosNewsUseCaseImpl(get(), get()) }
}
