package com.example.playlistmaker.util

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track

sealed interface PlaylistUiState {
    data object Loading : PlaylistUiState
    data class Content (val playlist:Playlist, val tracks:List<Track>): PlaylistUiState
}