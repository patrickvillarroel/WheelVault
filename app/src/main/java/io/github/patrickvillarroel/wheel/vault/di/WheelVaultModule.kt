package io.github.patrickvillarroel.wheel.vault.di

import io.github.patrickvillarroel.wheel.vault.ui.screen.camera.CameraPermissionViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.camera.CameraViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** The only exported module to Koin, this include all others modules */
val wheelVaultModule = module {
    // data-sources
    includes(supabaseModule)

    // presenter
    viewModel { CameraViewModel() }
    viewModel { CameraPermissionViewModel() }
}
