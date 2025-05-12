package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchTracksInteractor {
    fun searchTracks(expression: String): Flow<SearchResult>
}