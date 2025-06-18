package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.db.entity.TracksPlaylistsEntity

@Dao
interface TracksPlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addToPlaylist(trackPlaylist: TracksPlaylistsEntity)

    @Delete
    suspend fun removeFromPlaylist(trackPlaylist: TracksPlaylistsEntity)

    @Query("SELECT COUNT(trackId) FROM tracks_playlists_table WHERE playlistId=:playlistId")
    fun countTracks(playlistId:Int): Flow<Int>

    @Query("SELECT t.* FROM track_table t INNER JOIN tracks_playlists_table tp ON t.trackId = tp.trackId WHERE playlistId = :playlistId")
    fun getTracksInPlaylist(playlistId:Int): Flow<List<TrackEntity>>

    @Query("SELECT EXISTS (SELECT 1 FROM tracks_playlists_table WHERE trackId = :trackId LIMIT 1)")
    suspend fun trackInPlaylists(trackId:Int): Boolean
}