package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface PreferencesRepository {
    fun getSavedTheme():Boolean
    fun setTheme(theme: Boolean)
    fun getSavedHistory(): List<Track>
    fun saveHistory(trackList:List<Track>)
    fun clearHistory()
}