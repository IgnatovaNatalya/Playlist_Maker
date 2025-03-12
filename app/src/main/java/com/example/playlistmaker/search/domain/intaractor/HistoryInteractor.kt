package com.example.playlistmaker.search.domain.intaractor

import com.example.playlistmaker.search.domain.model.Track

interface HistoryInteractor
{
    fun getSavedHistory() : List<Track>
    fun saveHistory()
    fun clearHistory()
    fun getTracks(): List<Track>
    fun addTrackToHistory(track: Track)
}
