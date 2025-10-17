package io.github.patrickvillarroel.wheel.vault.di

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.ExternalAuthAction
import io.github.jan.supabase.coil.Coil3Integration
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.PropertyConversionMethod
import io.github.jan.supabase.storage.Storage
import io.github.patrickvillarroel.wheel.vault.BuildConfig
import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module

val supabaseModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
        ) {
            httpEngine = CIO.create()
            install(Auth) {
                scheme = "https"
                host = "wheel.supabase.com"
                defaultExternalAuthAction = ExternalAuthAction.CustomTabs()
            }
            install(Postgrest) {
                propertyConversionMethod = PropertyConversionMethod.SERIAL_NAME
            }
            install(Storage)
            install(Coil3Integration)
        }
    }
}
