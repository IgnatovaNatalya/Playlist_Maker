package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchTracksInteractor {
    fun searchTracks(expression: String): Flow<SearchResult>
}