package com.example.playlistmaker.data.history

import com.example.playlistmaker.domain.model.Track

interface HistoryRepository {
//    fun containsTheme():Boolean
//    fun getSavedTheme():Boolean
//    fun saveTheme(theme: Boolean)
    fun getSavedHistory(): List<Track>
    fun saveHistory(trackList:List<Track>)
    fun clearHistory()
}