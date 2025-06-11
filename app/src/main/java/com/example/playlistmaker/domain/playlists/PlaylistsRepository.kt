package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.AddToPlaylistResult
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun addToPlaylist(playlistId: Int, track: Track): AddToPlaylistResult
    suspend fun removeFromPlaylist(playlistId: Int, track: Track)

    fun getPlaylists(): Flow<List<Playlist>>
}