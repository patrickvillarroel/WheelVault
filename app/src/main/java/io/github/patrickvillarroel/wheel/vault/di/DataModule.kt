package io.github.patrickvillarroel.wheel.vault.di

import io.github.patrickvillarroel.wheel.vault.data.BrandRepositoryImpl
import io.github.patrickvillarroel.wheel.vault.data.CarRepositoryImpl
import io.github.patrickvillarroel.wheel.vault.data.GetVideosNewsUseCaseImpl
import io.github.patrickvillarroel.wheel.vault.data.UpdateOnboardingStateUseCaseImpl
import io.github.patrickvillarroel.wheel.vault.data.UpdateOnboardingStateUseCaseImpl.Companion.dataStore
import io.github.patrickvillarroel.wheel.vault.data.datasource.BrandSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.data.datasource.CarSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.github.patrickvillarroel.wheel.vault.domain.usecase.GetVideosNewsUseCase
import io.github.patrickvillarroel.wheel.vault.domain.usecase.UpdateOnboardingStateUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { androidContext().dataStore }
    single { BrandSupabaseDataSource(get(), get()) }
    single<BrandRepository> { BrandRepositoryImpl(get()) }

    single { CarSupabaseDataSource(get(), get()) }
    single<CarsRepository> { CarRepositoryImpl(get()) }
    single<UpdateOnboardingStateUseCase> { UpdateOnboardingStateUseCaseImpl(get()) }
    single<GetVideosNewsUseCase> { GetVideosNewsUseCaseImpl(get(), get()) }
}
