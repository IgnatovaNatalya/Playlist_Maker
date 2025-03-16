package com.example.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.player.data.repository.PlaybackRepository
import com.example.playlistmaker.player.data.repositoryImpl.PlaybackRepositoryImpl
import com.example.playlistmaker.player.domain.PlaybackInteractor
import com.example.playlistmaker.player.domain.impl.PlaybackInteractorImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.HistoryRepository
import com.example.playlistmaker.search.data.repository.TracksRepository
import com.example.playlistmaker.search.data.repositoryImpl.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.repositoryImpl.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.HistoryInteractor
import com.example.playlistmaker.search.domain.SearchTracksInteractor
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.SearchTracksInteractorImpl
import com.example.playlistmaker.settings.data.impl.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeRepository
import com.example.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl


object Creator {

    private const val PLAYLIST_MAKER_PREFS = "playlist_maker_prefs"

    var sharedPrefs: SharedPreferences? = null

    fun getSharedPreferences(context: Context): SharedPreferences {
        if (sharedPrefs == null)
            sharedPrefs = context.getSharedPreferences(PLAYLIST_MAKER_PREFS, Context.MODE_PRIVATE)
        return sharedPrefs!!
    }

    //search
    private fun getTracksRepository(): TracksRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideSearchTracksInteractor(): SearchTracksInteractor {
        return SearchTracksInteractorImpl(getTracksRepository())
    }

    //history
    private fun getHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepositoryImpl(getSharedPreferences(context))
    }

    fun provideHistoryInteractor(context: Context): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository(context))
    }

    //play
    private fun getPlaybackRepository(): PlaybackRepository {
        return PlaybackRepositoryImpl()
    }

    fun providePlaybackInteractor(): PlaybackInteractor {
        return PlaybackInteractorImpl(getPlaybackRepository())
    }

    //settings
    private fun getThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImpl(getSharedPreferences(context))
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context))
    }

    //share
    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl( getExternalNavigator(context))
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }
}
