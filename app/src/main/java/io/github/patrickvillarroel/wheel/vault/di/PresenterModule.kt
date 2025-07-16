package io.github.patrickvillarroel.wheel.vault.di

import io.github.patrickvillarroel.wheel.vault.ui.screen.camera.CameraPermissionViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.camera.CameraViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.login.LoginViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.session.SessionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presenterModule = module {
    viewModel { CameraViewModel() }
    viewModel { CameraPermissionViewModel() }
    viewModel { LoginViewModel(get()) }
    viewModel { SessionViewModel(get()) }
}
