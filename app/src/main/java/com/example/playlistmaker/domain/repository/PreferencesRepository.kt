package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface PreferencesRepository {
    fun containsTheme():Boolean
    fun getSavedTheme():Boolean
    fun saveTheme(theme: Boolean)
    fun getSavedHistory(): List<Track>
    fun saveHistory(trackList:List<Track>)
    fun clearHistory()
}