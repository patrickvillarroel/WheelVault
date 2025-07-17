package io.github.patrickvillarroel.wheel.vault.di

import io.github.patrickvillarroel.wheel.vault.data.BrandRepositoryImpl
import io.github.patrickvillarroel.wheel.vault.data.datasource.BrandSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.repository.BrandRepository
import org.koin.dsl.module

val dataModule = module {
    single { BrandSupabaseDataSource(get(), get()) }
    single<BrandRepository> { BrandRepositoryImpl(get()) }
}
