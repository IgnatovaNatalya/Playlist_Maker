package com.example.playlistmaker.search.ui.viewmodel


sealed interface SearchState {

    data object Loading : SearchState
    data object Content : SearchState
    data object Error : SearchState
    data object Empty : SearchState
}
