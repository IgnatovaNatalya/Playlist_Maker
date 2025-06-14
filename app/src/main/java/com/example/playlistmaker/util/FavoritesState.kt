package com.example.playlistmaker.util

import com.example.playlistmaker.domain.model.Track

sealed interface FavoritesState {
    data object Empty : FavoritesState
    data object Loading : FavoritesState
    data class Content (val favoriteTracks:List<Track>): FavoritesState
}