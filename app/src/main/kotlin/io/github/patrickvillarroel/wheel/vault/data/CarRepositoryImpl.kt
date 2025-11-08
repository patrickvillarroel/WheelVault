package io.github.patrickvillarroel.wheel.vault.data

import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.CarSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository

class CarRepositoryImpl(private val supabase: CarSupabaseDataSource) : CarsRepository by supabase
