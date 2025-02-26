package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repositoryImpl.PreferencesRepositoryImpl
import com.example.playlistmaker.data.repositoryImpl.TrackRepositoryImpl
import com.example.playlistmaker.domain.impl.PlaybackInteractorImpl
import com.example.playlistmaker.domain.impl.SavedHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.SavedThemeInteractorImpl
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.example.playlistmaker.domain.impl.ShareInteractorImpl
import com.example.playlistmaker.domain.interactor.PlaybackInteractor
import com.example.playlistmaker.domain.interactor.SavedHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.interactor.SavedThemeInteractor
import com.example.playlistmaker.domain.interactor.ShareInteractor
import com.example.playlistmaker.domain.repository.PreferencesRepository
import com.example.playlistmaker.domain.repository.TracksRepository


object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): SearchTracksInteractor {
        return SearchTracksInteractorImpl(getTracksRepository())
    }

    private fun getPreferencesRepository(context: Context): PreferencesRepository {
        return PreferencesRepositoryImpl(context)
    }

    fun provideHistoryInteractor(context: Context): SavedHistoryInteractor {
        return SavedHistoryInteractorImpl(getPreferencesRepository(context))
    }

    fun provideThemeInteractor(context:Context): SavedThemeInteractor {
        return SavedThemeInteractorImpl(getPreferencesRepository(context))
    }

    fun providePlaybackInteractor() : PlaybackInteractor {
        return PlaybackInteractorImpl()
    }

    fun provideShareInteractor(context:Context): ShareInteractor {
        return ShareInteractorImpl(context)
    }
}
