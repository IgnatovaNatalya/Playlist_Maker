package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.repository.PlaybackRepository
import com.example.playlistmaker.player.data.repositoryImpl.PlaybackRepositoryImpl
import com.example.playlistmaker.player.domain.PlaybackInteractor
import com.example.playlistmaker.player.domain.impl.PlaybackInteractorImpl
import com.example.playlistmaker.player.ui.viewmodel.PlaybackViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val PlayerKoinModule = module {
    single<PlaybackInteractor> { PlaybackInteractorImpl(get()) }
    single<PlaybackRepository> { PlaybackRepositoryImpl(get()) }
    single {  MediaPlayer()  }
    viewModel { PlaybackViewModel(get()) }
}