package com.example.playlistmaker.data.search

import com.example.playlistmaker.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchTracksRepository {
    fun searchTracks(expression: String): Flow<SearchResult>
}