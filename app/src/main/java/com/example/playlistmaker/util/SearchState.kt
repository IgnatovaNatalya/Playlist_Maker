package com.example.playlistmaker.util

import com.example.playlistmaker.domain.model.Track

sealed interface SearchState {

    data object Loading : SearchState

    data class SearchContent (val searchTracks: List<Track>): SearchState

    data object Error : SearchState
    data object Empty : SearchState
}



