package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createPlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist:PlaylistEntity)

    //@Query("SELECT* FROM playlist_table")
    @Query("SELECT p.id, p.title,p.description,p.path,count(tp.trackId) as numTracks FROM playlist_table p INNER JOIN tracks_playlists_table tp ON p.id = tp.playlistId")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

//    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")
//    fun getPlaylist(playlistId:Int): Flow<PlaylistEntity>
}