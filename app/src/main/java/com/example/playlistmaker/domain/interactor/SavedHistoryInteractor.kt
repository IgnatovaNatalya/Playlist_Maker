package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track

interface SavedHistoryInteractor
{
    fun getSavedHistory() : List<Track>

    fun saveHistory(historyTracks: List<Track>)

    fun clearHistory()
}
