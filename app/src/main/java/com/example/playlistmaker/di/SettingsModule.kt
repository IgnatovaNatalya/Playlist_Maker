package com.example.playlistmaker.di

import com.example.playlistmaker.data.settings.ThemeRepositoryImpl
import com.example.playlistmaker.domain.settings.ThemeInteractor
import com.example.playlistmaker.domain.settings.ThemeRepository
import com.example.playlistmaker.domain.settings.ThemeInteractorImpl
import com.example.playlistmaker.viewmodel.SettingsViewModel
import com.example.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    factory<ThemeInteractor> { ThemeInteractorImpl(get()) }
    factory<SharingInteractor> { SharingInteractorImpl(get()) }
    factory<ThemeRepository> { ThemeRepositoryImpl(get()) }
    factory<ExternalNavigator> { ExternalNavigatorImpl(androidContext()) }
    viewModel { SettingsViewModel(get(), get()) }
}