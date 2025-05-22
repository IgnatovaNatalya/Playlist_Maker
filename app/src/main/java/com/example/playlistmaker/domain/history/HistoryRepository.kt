package com.example.playlistmaker.domain.history

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun addToHistoryAndTrim(track:Track, limit:Int)
    suspend fun clearHistory()
    fun getHistory(): Flow<List<Track>>

}