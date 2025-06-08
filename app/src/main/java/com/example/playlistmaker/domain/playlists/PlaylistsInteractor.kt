package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun addToPlaylist(playlistId:Int, track: Track)
    suspend fun removeFromPlaylist(playlistId:Int, track: Track)

    fun getPlaylist(playlistId: Int):Flow<Playlist>
}