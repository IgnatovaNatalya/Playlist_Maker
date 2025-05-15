package com.example.playlistmaker.domain.search

import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.domain.SearchTracksInteractor
import com.example.playlistmaker.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

class SearchTracksInteractorImpl(private val repository: TracksRepository) :
    SearchTracksInteractor {

    override fun searchTracks(expression: String): Flow<SearchResult> {
        return repository.searchTracks(expression)
    }
}