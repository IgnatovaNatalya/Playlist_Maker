package com.example.playlistmaker.data.playlists

import android.database.sqlite.SQLiteConstraintException
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TracksPlaylistsEntity
import com.example.playlistmaker.data.db.entity.toListTrack
import com.example.playlistmaker.data.dto.toListPlaylist
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.util.AddToPlaylistResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase) : PlaylistsRepository {

    override suspend fun createPlaylist(title: String, description: String, path: String) {
        val playlistEntity = PlaylistEntity(
            id = 0,
            title = title,
            description = description,
            path = path
        )
        appDatabase.playlistDao().createPlaylist(playlistEntity)
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

    override suspend fun deletePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        getPlaylistTracks(playlist.id).collect { tracks ->
            tracks.forEach { track ->
                removeTrackFromPlaylist(track, playlist.id)
            }
            appDatabase.playlistDao().deletePlaylist(playlist.id)
        }
    }

    override suspend fun removeTrackFromPlaylist(track: Track, playlistId:Int) {
        removeFromPlaylist(track, playlistId)
        if (!trackInPlaylists(track.trackId))
            appDatabase.trackDao().deleteTrack(track.toTrackEntity())
    }

    private suspend fun trackInPlaylists (trackId:Int):Boolean {
        return appDatabase.tracksPlaylistsDao().trackInPlaylists(trackId)
    }

    private suspend fun removeFromPlaylist(track: Track, playlistId: Int,) {
        val trackPlaylistEntity = TracksPlaylistsEntity(track.trackId, playlistId)
        appDatabase.tracksPlaylistsDao().removeFromPlaylist(trackPlaylistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { toListPlaylist(it) }
    }

    override fun getPlaylist(playlistId: Int): Flow<Playlist> {
        return appDatabase.playlistDao().getPlaylist(playlistId).map { it.toPlaylist() }
    }

    override fun getPlaylistTracks(playlistId: Int): Flow<List<Track>> {
        return appDatabase.playlistDao().getPlaylistTracks(playlistId).map{ toListTrack(it) }
    }
}