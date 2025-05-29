package com.example.playlistmaker.domain.history

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

class HistoryInteractorImpl (private val historyRepository: HistoryRepository) :
    HistoryInteractor {

    override fun getHistory(): Flow<List<Track>> {
        return historyRepository.getHistory()
    }

    override suspend fun clearHistory() {
        historyRepository.clearHistory()
    }

    override suspend fun addTrackToHistory(track: Track) {
        historyRepository.addToHistoryAndTrim(track, HISTORY_LIMIT)
    }

    companion object {
        const val HISTORY_LIMIT = 10
    }
}