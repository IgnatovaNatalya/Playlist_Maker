package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val PLAYLIST_MAKER_HISTORY = "playlist_maker_history"
const val HISTORY_LIMIT = 10

class SearchHistory(private val prefs: SharedPreferences) {

    private var trackList = mutableListOf<Track>()
    private val gson = Gson()

    fun getSavedHistory(): MutableList<Track> {
        val str = prefs.getString(PLAYLIST_MAKER_HISTORY, null)
        if (str != null) trackList = createTracksListFromJson(str)
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
        prefs.edit()
            .remove(PLAYLIST_MAKER_HISTORY)
            .apply()
    }

    fun saveHistory() {
        val str = createJsonFromTrackList(trackList)
        prefs.edit()
            .putString(PLAYLIST_MAKER_HISTORY, str)
            .apply()
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

    private fun createJsonFromTrackList(facts: MutableList<Track>): String {
        return gson.toJson(facts)
    }

    private fun createTracksListFromJson(json: String): MutableList<Track> {
        return mutableListOf<Track>().apply {
            addAll( gson.fromJson(json,Array<Track>::class.java )
            )}
    }
}