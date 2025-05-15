package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.history.HistoryInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.data.history.HistoryRepository

const val HISTORY_LIMIT = 10

class HistoryInteractorImpl (private val repository: HistoryRepository) :
    HistoryInteractor {

    private var historyTrackList = mutableListOf<Track>()

    override fun getSavedHistory() : List<Track> {
        historyTrackList.clear()
        historyTrackList.addAll(repository.getSavedHistory())
        return historyTrackList
    }

    override fun getTracks() :List<Track> {
        return historyTrackList
    }

    override fun addTrackToHistory(track: Track) {
        if (indexOF(track.trackId) != null)
            historyTrackList.remove(track)
        else if (historyTrackList.size >= HISTORY_LIMIT)
            historyTrackList.removeAt(historyTrackList.lastIndex)
        historyTrackList.add(0, track)
        saveHistory()
    }
    override fun saveHistory() {
        repository.saveHistory(historyTrackList)
    }

    override fun clearHistory() {
        historyTrackList.clear()
        repository.clearHistory()
    }

    private fun indexOF(trackId: Int): Int? {
        for ((index, track) in historyTrackList.withIndex()) {
            if (track.trackId == trackId) return index
        }
        return null
    }
}