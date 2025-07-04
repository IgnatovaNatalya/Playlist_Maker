package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.data.player.PlaybackRepository
import com.example.playlistmaker.data.player.PlaybackRepositoryImpl
import com.example.playlistmaker.domain.player.PlaybackInteractor
import com.example.playlistmaker.domain.player.PlaybackInteractorImpl
import com.example.playlistmaker.viewmodel.PlaybackViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    factory<PlaybackInteractor> { PlaybackInteractorImpl(get()) }
    factory<PlaybackRepository> { PlaybackRepositoryImpl(get()) }
    factory { MediaPlayer() }

    viewModel { PlaybackViewModel(get(), get(), get()) }
}