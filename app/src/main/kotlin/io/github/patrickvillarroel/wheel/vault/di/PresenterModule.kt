package io.github.patrickvillarroel.wheel.vault.di

import io.github.patrickvillarroel.wheel.vault.ui.screen.BrandViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.CarViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.camera.CameraViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.ExchangeViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.history.ExchangeHistoryViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.notifications.ExchangeNotificationsViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.notifications.TradeProposalDetailViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.exchanges.selection.ExchangeCarSelectionViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.garage.GarageViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.home.HomeViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.login.LoginViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.session.SessionViewModel
import io.github.patrickvillarroel.wheel.vault.ui.screen.splash.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presenterModule = module {
    viewModel { CameraViewModel() }
    viewModel { LoginViewModel(get()) }
    viewModel { SessionViewModel(get()) }
    viewModel { BrandViewModel(get(), get()) }
    viewModel { CarViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { GarageViewModel(get()) }
    // ExchangeViewModel needs to be shared across navigation screens
    single { ExchangeViewModel(get(), get()) }
    viewModel { ExchangeCarSelectionViewModel(get(), get()) }
    viewModel { ExchangeNotificationsViewModel(get(), get(), get()) }
    viewModel { ExchangeHistoryViewModel(get()) }
    viewModel { TradeProposalDetailViewModel(get(), get(), get()) }
}
