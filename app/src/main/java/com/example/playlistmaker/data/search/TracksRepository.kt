package com.example.playlistmaker.data.search

import com.example.playlistmaker.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<SearchResult>
}