package com.example.playlistmaker.util

import com.example.playlistmaker.domain.model.Playlist

sealed interface NewPlaylistUiState {
    data object Creating : NewPlaylistUiState
    data class Exist (val playlist:Playlist): NewPlaylistUiState
}