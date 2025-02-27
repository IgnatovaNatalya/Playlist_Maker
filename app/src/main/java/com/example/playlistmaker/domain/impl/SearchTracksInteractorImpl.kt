package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.repository.TracksRepository
import java.util.concurrent.Executors

class SearchTracksInteractorImpl(private val repository: TracksRepository) :
    SearchTracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: SearchTracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume( repository.searchTracks(expression) )
        }
    }
}