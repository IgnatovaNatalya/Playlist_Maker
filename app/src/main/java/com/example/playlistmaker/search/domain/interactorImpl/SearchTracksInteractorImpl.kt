package com.example.playlistmaker.search.domain.interactorImpl

import com.example.playlistmaker.search.domain.intaractor.SearchTracksInteractor
import com.example.playlistmaker.search.data.repository.TracksRepository
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