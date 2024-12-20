package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val PLAYLIST_MAKER_HISTORY = "playlist_maker_history"
const val HISTORY_LIMIT = 3 // todo 10

class SearchHistory(val prefs: SharedPreferences) {

    private var trackList = ArrayList<Track>()

    fun getSavedHistory(): ArrayList<Track> {
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

    fun getTracks(): ArrayList<Track> {
        return trackList
    }

    private fun indexOF(trackId: Int): Int? {
        for ((index, track) in trackList.withIndex()) {
            if (track.trackId == trackId) return index
        }
        return null
    }

    private fun createJsonFromTrackList(facts: ArrayList<Track>): String {
        return Gson().toJson(facts)
    }

    private fun createTracksListFromJson(json: String): ArrayList<Track> {
        return Gson().fromJson(json, Array<Track>::class.java).toCollection(ArrayList())
    }

}