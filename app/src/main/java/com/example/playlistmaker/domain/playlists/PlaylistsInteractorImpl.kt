package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.AddToPlaylistResult
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository) :
    PlaylistsInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistsRepository.createPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistsRepository.deletePlaylist(playlist)
    }

    override suspend fun addToPlaylist(
        playlistId: Int,
        track: Track
    ): AddToPlaylistResult {
        return playlistsRepository.addToPlaylist(playlistId, track)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override fun getPlaylist(playlistId: Int): Flow<Playlist> {
        return playlistsRepository.getPlaylist(playlistId)
    }

    override fun getPlaylistTracks(playlistId: Int): Flow<List<Track>> {
        return playlistsRepository.getPlaylistTracks(playlistId)
    }

    override suspend fun removeTrackFromPlaylist(track: Track, playlistId:Int) {
        playlistsRepository.removeTrackFromPlaylist(track, playlistId)
    }

}