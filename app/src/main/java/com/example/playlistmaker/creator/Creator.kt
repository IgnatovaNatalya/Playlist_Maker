package com.example.playlistmaker.creator

import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient


object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): SearchTracksInteractor {
        return SearchTracksInteractorImpl(getTracksRepository())
    }
}
