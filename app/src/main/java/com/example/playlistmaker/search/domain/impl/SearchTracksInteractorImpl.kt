package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.data.repository.TracksRepository
import com.example.playlistmaker.search.domain.SearchTracksInteractor
import com.example.playlistmaker.search.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

class SearchTracksInteractorImpl(private val repository: TracksRepository) :
    SearchTracksInteractor {

    override fun searchTracks(expression: String): Flow<SearchResult> {
        return repository.searchTracks(expression)
    }
}