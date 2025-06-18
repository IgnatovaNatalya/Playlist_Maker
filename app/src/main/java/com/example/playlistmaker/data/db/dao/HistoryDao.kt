package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.data.db.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToHistory(track: HistoryEntity)

    @Query("DELETE FROM history_track_table WHERE trackId " +
            "NOT IN (SELECT trackId FROM history_track_table ORDER BY added DESC LIMIT :limit)")
    suspend fun trimHistory(limit: Int)

    @Transaction
    suspend fun addToHistoryAndTrim(track: HistoryEntity, limit: Int ) {
        addToHistory(track)
        trimHistory(limit)
    }

    @Delete(entity = HistoryEntity::class)
    suspend fun removeFromHistory(track: HistoryEntity)

    @Query("DELETE FROM history_track_table")
    suspend fun clearHistory()

    @Query("SELECT h.trackId, h.trackName, h.artistName, h.trackTimeMillis, h.artworkUrl100, h.collectionName, " +
            "h.releaseDate, h.primaryGenreName, h.country, h.previewUrl, h.added, f.added is not null AS isFavorite " +
            "FROM history_track_table h LEFT JOIN fav_track_table f ON h.trackId = f.trackId " +
            "ORDER BY h.added DESC")
    fun getHistoryTracks(): Flow<List<HistoryEntity>>

}