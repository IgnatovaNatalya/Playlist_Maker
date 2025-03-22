package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.domain.model.Track

interface HistoryRepository {
//    fun containsTheme():Boolean
//    fun getSavedTheme():Boolean
//    fun saveTheme(theme: Boolean)
    fun getSavedHistory(): List<Track>
    fun saveHistory(trackList:List<Track>)
    fun clearHistory()
}