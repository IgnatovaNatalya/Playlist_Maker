package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track

interface SearchTracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }
}