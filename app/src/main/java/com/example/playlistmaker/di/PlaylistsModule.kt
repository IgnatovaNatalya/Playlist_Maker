package com.example.playlistmaker.di

import com.example.playlistmaker.data.internalStorage.InternalStorageRepositoryImpl
import com.example.playlistmaker.data.playlists.PlaylistsRepositoryImpl
import com.example.playlistmaker.domain.internalStorage.InternalStorageInteractor
import com.example.playlistmaker.domain.internalStorage.InternalStorageInteractorImpl
import com.example.playlistmaker.domain.internalStorage.InternalStorageRepository
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsInteractorImpl
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.viewmodel.NewPlaylistViewModel
import com.example.playlistmaker.viewmodel.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistsModule = module {
    factory<PlaylistsInteractor> { PlaylistsInteractorImpl(get()) }
    factory<PlaylistsRepository> { PlaylistsRepositoryImpl(get()) }
    factory<InternalStorageInteractor> { InternalStorageInteractorImpl(get()) }
    factory<InternalStorageRepository> { InternalStorageRepositoryImpl(get()) }

    viewModel { NewPlaylistViewModel(get(), get()) }

    viewModel { PlaylistViewModel(get()) }
}