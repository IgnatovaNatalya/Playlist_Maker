package com.example.playlistmaker.domain.history

import com.example.playlistmaker.domain.model.Track

interface HistoryInteractor
{
    fun getSavedHistory() : List<Track>
    fun saveHistory()
    fun clearHistory()
    fun getTracks(): List<Track>
    fun addTrackToHistory(track: Track)
}