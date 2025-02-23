package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.interactor.SavedHistoryInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PreferencesRepository

class SavedHistoryInteractorImpl (private val repository: PreferencesRepository) :
    SavedHistoryInteractor {


    override fun getSavedHistory() : List<Track> {
        return repository.getSavedHistory()
    }

    override fun saveHistory(historyTracks: List<Track>) {
        repository.saveHistory(historyTracks)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}