package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track

interface HistoryInteractor
{
    fun getSavedHistory() : List<Track>
    fun saveHistory()
    fun clearHistory()
    fun getTracks(): List<Track>
    fun addTrackToHistory(track: Track)
}
