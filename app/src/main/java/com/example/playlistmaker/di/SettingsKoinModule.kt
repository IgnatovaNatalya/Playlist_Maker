package com.example.playlistmaker.di

import com.example.playlistmaker.settings.data.impl.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeRepository
import com.example.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val SettingsKoinModule = module {
    single<ThemeInteractor> { ThemeInteractorImpl(get()) }
    single<SharingInteractor> { SharingInteractorImpl(get()) }
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<ExternalNavigator> { ExternalNavigatorImpl(androidContext()) }
    viewModel { SettingsViewModel(get(), get()) }
}