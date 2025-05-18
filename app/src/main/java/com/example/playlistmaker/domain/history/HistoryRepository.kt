package com.example.playlistmaker.domain.history

import com.example.playlistmaker.domain.model.Track

interface HistoryRepository {
    fun getSavedHistory(): List<Track>
    fun saveHistory(trackList:List<Track>)
    fun clearHistory()
}