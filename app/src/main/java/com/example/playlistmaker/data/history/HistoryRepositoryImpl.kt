package com.example.playlistmaker.data.history

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.toListTrack
import com.example.playlistmaker.domain.history.HistoryRepository
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(
    private val appDatabase: AppDatabase
) :
    HistoryRepository {

    override fun getHistory(): Flow<List<Track>> {
        val historyTracks = appDatabase.historyDao().getHistoryTracks()
        return historyTracks.map { toListTrack(it) }
    }

    override suspend fun addToHistoryAndTrim(track: Track, limit: Int) {
        val historyEntity = track.toHistoryEntity(track)
        appDatabase.historyDao().addToHistoryAndTrim(historyEntity, limit)
    }

    override suspend fun clearHistory() {
        appDatabase.historyDao().clearHistory()
    }
}