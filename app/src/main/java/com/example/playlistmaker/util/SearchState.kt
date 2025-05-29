package com.example.playlistmaker.util


sealed interface SearchState {

    data object Loading : SearchState
    data object SearchContent : SearchState
    data object HistoryContent : SearchState
    data object Error : SearchState
    data object Empty : SearchState
}





