package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.dto.PlaylistDto
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createPlaylist(playlist: PlaylistEntity)

    //@Delete
    @Query("DELETE FROM playlist_table WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Int)

    @Query(
        "SELECT p.id, p.title, p.description, p.path, count(tp.trackId) as numTracks , 0 as totalDurationMillis " +
                "FROM playlist_table p " +
                "LEFT JOIN tracks_playlists_table tp ON p.id = tp.playlistId " +
                "GROUP BY p.id ORDER BY p.id DESC"
    )
    fun getPlaylists(): Flow<List<PlaylistDto>>

    @Query(
        "SELECT p.id, p.title, p.description, p.path, count(tp.trackId) as numTracks, " +
                "sum(t.trackTimeMillis) as totalDurationMillis " +
                "FROM playlist_table p " +
                "LEFT JOIN tracks_playlists_table tp ON p.id = tp.playlistId " +
                "LEFT JOIN track_table t ON tp.trackId = t.trackId " +
                "WHERE p.id = :playlistId"
    )
    fun getPlaylist(playlistId: Int): Flow<PlaylistDto>

    @Query("SELECT t.* FROM  track_table t LEFT JOIN tracks_playlists_table tp " +
            "ON t.trackId = tp.trackId WHERE tp.playlistId = :playlistId")
    fun getPlaylistTracks(playlistId: Int): Flow<List<TrackEntity>>
}