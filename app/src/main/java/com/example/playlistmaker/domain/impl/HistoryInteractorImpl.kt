package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.interactor.HistoryInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PreferencesRepository

const val HISTORY_LIMIT = 10

class HistoryInteractorImpl (private val repository: PreferencesRepository) :
    HistoryInteractor {

    private var historyTrackList = mutableListOf<Track>()

    override fun getSavedHistory() : List<Track> {
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