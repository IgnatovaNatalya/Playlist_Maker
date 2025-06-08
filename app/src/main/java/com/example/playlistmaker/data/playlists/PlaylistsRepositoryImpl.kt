package com.example.playlistmaker.data.playlists

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.TracksPlaylistsEntity
import com.example.playlistmaker.data.db.entity.toListTrack
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase) : PlaylistsRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().createPlaylist(playlist.toPlaylistEntity())
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(playlist.toPlaylistEntity())
    }

    override suspend fun addToPlaylist(
        playlistId: Int,
        track: Track
    ) {
        val trackEntity = track.toTrackEntity()
        val trackPlaylistEntity = TracksPlaylistsEntity(
            trackId = track.trackId, playlistId = playlistId,
            id = 0
        )
        appDatabase.trackDao().addTrack(trackEntity)
        appDatabase.tracksPlaylistsDao().addToPlaylist(trackPlaylistEntity)
    }

    override suspend fun removeFromPlaylist(
        playlistId: Int,
        track: Track
    ) {
        val trackPlaylistEntity = TracksPlaylistsEntity(0, track.trackId, playlistId)
        appDatabase.tracksPlaylistsDao().removeFromPlaylist(trackPlaylistEntity)
    }

    override fun getPlaylist(playlistId: Int): Flow<Playlist> {
        val listTrackEntityFlow = appDatabase.tracksPlaylistsDao().getTracksInPlaylist(playlistId)
        val playlistsEntityFlow = appDatabase.playlistDao().getPlaylist(playlistId)

        return combine(playlistsEntityFlow, listTrackEntityFlow) { playlist, tracks ->
            with(playlist) {
                Playlist(
                    id,
                    title,
                    description,
                    path,
                    numberTracks,
                    toListTrack(tracks)
                )
            }
        }
    }
}