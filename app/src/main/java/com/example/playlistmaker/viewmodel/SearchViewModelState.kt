package com.example.playlistmaker.viewmodel


sealed interface SearchViewModelState {
    data object Search : SearchViewModelState
    data object History: SearchViewModelState
}