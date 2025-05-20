package com.example.playlistmaker.domain.history

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getHistory(): Flow<List<Track>>
    suspend fun addToHistory(track:Track)
    suspend fun clearHistory()
}