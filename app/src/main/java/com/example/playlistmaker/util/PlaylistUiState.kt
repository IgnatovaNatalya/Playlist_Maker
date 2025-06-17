package com.example.playlistmaker.util

import com.example.playlistmaker.domain.model.Playlist

sealed interface PlaylistUiState {
    ///data object Empty : PlaylistUiState
    data object Loading : PlaylistUiState
    data class Content (val playlist:Playlist): PlaylistUiState
}