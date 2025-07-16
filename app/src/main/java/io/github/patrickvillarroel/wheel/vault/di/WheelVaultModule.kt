package io.github.patrickvillarroel.wheel.vault.di

import org.koin.dsl.module

/** The only exported module to Koin, this include all others modules */
val wheelVaultModule = module {
    // data-sources
    includes(supabaseModule)

    // presenter
    includes(presenterModule)
}
