package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.interactor.SavedHistoryInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PreferencesRepository

const val HISTORY_LIMIT = 10

class SavedHistoryInteractorImpl (private val repository: PreferencesRepository) :
    SavedHistoryInteractor {

    private var trackList = mutableListOf<Track>()

    override fun getSavedHistory() : List<Track> {
        trackList.addAll(repository.getSavedHistory())
        return trackList
    }

    override fun getTracks() :List<Track> {
        return trackList
    }

    override fun addTrackToHistory(track: Track) {
        if (indexOF(track.trackId) != null)
            trackList.remove(track)
        else if (trackList.size >= HISTORY_LIMIT)
            trackList.removeAt(trackList.lastIndex)
        trackList.add(0, track)
        saveHistory()
    }
    override fun saveHistory() {
        repository.saveHistory(trackList)
    }

    override fun clearHistory() {
        trackList.clear()
        repository.clearHistory()
    }

    private fun indexOF(trackId: Int): Int? {
        for ((index, track) in trackList.withIndex()) {
            if (track.trackId == trackId) return index
        }
        return null
    }
}