package com.example.playlistmaker.util

import com.example.playlistmaker.domain.model.Track


sealed interface SearchState {

    data object Loading : SearchState
    data class SearchContent(val foundTracks: List<Track>) : SearchState
    data class HistoryContent(val historyTracks: List<Track>) : SearchState
    data object Error : SearchState
    data object NotFound : SearchState
    data object Empty : SearchState
}






