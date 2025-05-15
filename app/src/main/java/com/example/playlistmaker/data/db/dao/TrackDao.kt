package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Delete(entity = TrackEntity::class)
    fun removeTrack(trackEntity: TrackEntity)

    @Query("SELECT * FROM fav_track_table")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM fav_track_table")
    suspend fun getTrackIds(): List<String>
}