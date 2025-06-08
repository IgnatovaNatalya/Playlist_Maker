package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.data.player.PlaybackRepository
import com.example.playlistmaker.data.player.PlaybackRepositoryImpl
import com.example.playlistmaker.data.playlists.PlaylistsRepositoryImpl
import com.example.playlistmaker.domain.player.PlaybackInteractor
import com.example.playlistmaker.domain.player.PlaybackInteractorImpl
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsInteractorImpl
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.viewmodel.NewPlaylistViewModel
import com.example.playlistmaker.viewmodel.PlaybackViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistsModule = module {
    factory<PlaylistsInteractor> { PlaylistsInteractorImpl(get()) }
    factory<PlaylistsRepository> { PlaylistsRepositoryImpl(get()) }

    viewModel { NewPlaylistViewModel( get()) }
}