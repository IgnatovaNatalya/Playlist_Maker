package com.example.playlistmaker.data.playlists

import android.database.sqlite.SQLiteConstraintException
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.TracksPlaylistsEntity
import com.example.playlistmaker.data.db.entity.toListPlaylist
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.util.AddToPlaylistResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
    ): AddToPlaylistResult {
        val trackEntity = track.toTrackEntity()
        val trackPlaylistEntity = TracksPlaylistsEntity(
            trackId = track.trackId, playlistId = playlistId
        )
        appDatabase.trackDao().addTrack(trackEntity)

        return try {
            appDatabase.tracksPlaylistsDao().addToPlaylist(trackPlaylistEntity)
            AddToPlaylistResult.Success
        } catch (e: SQLiteConstraintException) {
            if (e is SQLiteConstraintException && e.message?.contains("UNIQUE") == true) {
                AddToPlaylistResult.AlreadyExists
            } else {
                AddToPlaylistResult.Error(e)
            }
        } catch (e: Exception) {
            AddToPlaylistResult.Error(e)
        }
    }

    override suspend fun removeFromPlaylist(
        playlistId: Int,
        track: Track
    ) {
        val trackPlaylistEntity = TracksPlaylistsEntity(track.trackId, playlistId)
        appDatabase.tracksPlaylistsDao().removeFromPlaylist(trackPlaylistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { toListPlaylist(it) }
    }

    override fun getPlaylist(playlistId: Int): Flow<Playlist> {
        return appDatabase.playlistDao().getPlaylist(playlistId).map { it.toPlaylist() }
    }
}