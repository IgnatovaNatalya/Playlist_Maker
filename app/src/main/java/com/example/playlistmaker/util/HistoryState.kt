package com.example.playlistmaker.util

import com.example.playlistmaker.domain.model.Track

sealed interface HistoryState {
    data object Loading : HistoryState
    data object Empty : HistoryState
    data class HistoryContent(val historyTracks: List<Track>) : HistoryState
}