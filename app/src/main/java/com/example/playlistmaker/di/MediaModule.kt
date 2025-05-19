package com.example.playlistmaker.di

import com.example.playlistmaker.viewmodel.FavoritesViewModel
import com.example.playlistmaker.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    viewModel { FavoritesViewModel(get()) }
    viewModel { PlaylistsViewModel() }
}