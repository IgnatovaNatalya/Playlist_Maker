package com.example.playlistmaker.di

import com.example.playlistmaker.data.settings.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeRepository
import com.example.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val SettingsKoinModule = module {
    factory<ThemeInteractor> { ThemeInteractorImpl(get()) }
    factory<SharingInteractor> { SharingInteractorImpl(get()) }
    factory<ThemeRepository> { ThemeRepositoryImpl(get()) }
    factory<ExternalNavigator> { ExternalNavigatorImpl(androidContext()) }
    viewModel { SettingsViewModel(get(), get()) }
}