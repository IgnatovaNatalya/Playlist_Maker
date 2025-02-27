package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.SearchResult

interface SearchTracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(searchResult: SearchResult)
    }
}