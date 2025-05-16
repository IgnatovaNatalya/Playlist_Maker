package com.example.playlistmaker.data.history

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.history.HistoryRepository
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class HistoryRepositoryImpl(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson,
    private val favoritesInteractor: FavoritesInteractor
) :
    HistoryRepository {

    companion object {
        const val PLAYLIST_MAKER_HISTORY = "playlist_maker_history"
    }

    override fun getSavedHistory(): Flow<List<Track>> = flow {
        val str = sharedPrefs.getString(PLAYLIST_MAKER_HISTORY, null)
        if (str != null) {
            val savedTracks = createTracksListFromJson(str)

            CoroutineScope(Dispatchers.IO).launch {
                val favIds = async { favoritesInteractor.getFavoriteTrackIds() }.await()
                for (track in savedTracks)
                    if (favIds.contains(track.trackId)) track.isFavorite = true
            }
            emit(savedTracks)

//            CoroutineScope(Dispatchers.IO).launch {
//                favoritesInteractor.getFavoriteTrackIds()
//                    .collect { favIds ->
//                        for (track in savedTracks)
//                            if (track.trackId in favIds) track.isFavorite = true
//                    }
//            }

        }
        emit(listOf())
    }

    override fun saveHistory(trackList: List<Track>) {
        val str = createJsonFromTrackList(trackList)
        sharedPrefs.edit {
            putString(PLAYLIST_MAKER_HISTORY, str)
        }
    }

    override fun clearHistory() {
        sharedPrefs.edit {
            remove(PLAYLIST_MAKER_HISTORY)
        }
    }

    private fun createJsonFromTrackList(facts: List<Track>): String {
        return gson.toJson(facts)
    }

    private fun createTracksListFromJson(json: String): List<Track> {
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }
}