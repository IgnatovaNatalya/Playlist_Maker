package com.example.playlistmaker.domain.history

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface HistoryInteractor {
    fun getHistory(): Flow<List<Track>>
    suspend fun clearHistory()
    suspend fun addTrackToHistory(track: Track)
}