package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface FavoritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun removeFromFavorites(trackEntity: TrackEntity)

    @Query("SELECT * FROM fav_track_table")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM fav_track_table")
    suspend fun getFavoriteTrackIds():List<Int>
}