package com.example.playlistmaker.data.history

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson

class HistoryRepositoryImpl(private val sharedPrefs: SharedPreferences, private val gson: Gson) :
    HistoryRepository {

    companion object {
        const val PLAYLIST_MAKER_HISTORY = "playlist_maker_history"
    }

    override fun getSavedHistory(): List<Track> {
        val str = sharedPrefs.getString(PLAYLIST_MAKER_HISTORY, null)
        if (str != null) return createTracksListFromJson(str)
        return listOf()
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