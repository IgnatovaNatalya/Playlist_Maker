package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.AddToPlaylistResult
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun createPlaylist(title: String, description: String, path: String)
    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun addToPlaylist(playlistId: Int, track: Track): AddToPlaylistResult

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylist(playlistId: Int): Flow<Playlist>
    fun getPlaylistTracks(playlistId:Int): Flow<List<Track>>

    suspend fun removeTrackFromPlaylist(track: Track, playlistId:Int)

}