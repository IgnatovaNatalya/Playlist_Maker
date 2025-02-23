package com.example.playlistmaker.ui.search

import com.example.playlistmaker.domain.interactor.SavedHistoryInteractor
import com.example.playlistmaker.domain.model.Track

const val HISTORY_LIMIT = 10

class SearchHistory(private val historyInteractor: SavedHistoryInteractor) {

    private var trackList = mutableListOf<Track>()

    fun getSavedHistory(): List<Track> {
        trackList.addAll(historyInteractor.getSavedHistory())
        return trackList
    }

    fun addTrackToHistory(track: Track) {
        if (indexOF(track.trackId) != null)
            trackList.remove(track)
        else if (trackList.size >= HISTORY_LIMIT)
            trackList.removeAt(trackList.lastIndex)
        trackList.add(0, track)
        saveHistory()
    }

    fun clearHistory() {
        trackList.clear()
        historyInteractor.clearHistory()
    }

    fun saveHistory() {
        historyInteractor.saveHistory(trackList)
    }

    fun getTracks(): MutableList<Track>{
        return trackList
    }

    private fun indexOF(trackId: Int): Int? {
        for ((index, track) in trackList.withIndex()) {
            if (track.trackId == trackId) return index
        }
        return null
    }

}