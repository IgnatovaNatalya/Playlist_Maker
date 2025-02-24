package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.SearchResult

interface TracksRepository {
    fun searchTracks(expression: String): SearchResult
}


