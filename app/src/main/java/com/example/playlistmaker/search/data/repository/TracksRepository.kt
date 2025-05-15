package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<SearchResult>
}


