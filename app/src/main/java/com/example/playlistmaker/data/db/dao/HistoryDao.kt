package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToHistory(track: HistoryEntity)

    @Delete(entity = HistoryEntity::class)
    suspend fun removeFromHistory(track: HistoryEntity)

    @Query("DELETE FROM history_track_table")
    suspend fun clearHistory()

    @Query("SELECT * FROM history_track_table ORDER BY added DESC")
    suspend fun getHistoryTracks(): List<HistoryEntity>

}