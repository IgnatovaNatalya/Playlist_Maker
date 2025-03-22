package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.domain.model.SearchResult

interface TracksRepository {
    fun searchTracks(expression: String): SearchResult
}


