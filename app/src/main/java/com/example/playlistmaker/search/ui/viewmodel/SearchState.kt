package com.example.playlistmaker.search.ui.viewmodel

import com.example.playlistmaker.search.domain.model.Track


sealed interface SearchState {

    data object Loading : SearchState

    data class SearchContent (val searchTracks:List<Track>): SearchState
    data class HistoryContent (val historyTracks: List<Track>): SearchState

    data object Error : SearchState
    data object Empty : SearchState
}
