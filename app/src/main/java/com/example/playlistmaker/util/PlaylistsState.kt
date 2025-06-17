package com.example.playlistmaker.util

import com.example.playlistmaker.domain.model.Playlist

sealed interface PlaylistsState {
    data object Empty : PlaylistsState
    data object Loading : PlaylistsState
    data class Content (val playlists:List<Playlist>): PlaylistsState
}