package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.FavTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(track: FavTrackEntity)

    @Delete(entity = FavTrackEntity::class)
    suspend fun removeFromFavorites(trackId: FavTrackEntity)

    @Query("SELECT * FROM fav_track_table ORDER BY added DESC")
    fun getFavoriteTracks(): Flow<List<FavTrackEntity>>

    @Query("SELECT trackId FROM fav_track_table")
    suspend fun getFavoriteTrackIds():List<Int>
}