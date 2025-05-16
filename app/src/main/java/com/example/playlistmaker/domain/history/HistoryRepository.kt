package com.example.playlistmaker.domain.history

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getSavedHistory(): Flow<List<Track>>
    fun saveHistory(trackList:List<Track>)
    fun clearHistory()
}