package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val PLAYLIST_MAKER_HISTORY= "playlist_maker_history"

class SearchHistory (val prefs: SharedPreferences) {

    private var trackList = ArrayList<Track>()

    //получение сохранённых треков
    fun getSavedHistory():ArrayList<Track> {
        val str = prefs.getString(PLAYLIST_MAKER_HISTORY, null)
        if (str!=null) trackList = createTracksListFromJson(str)
        return trackList
    }

    // передача элемента для сохранения
    fun addTrackToHistory(track:Track) {
        trackList.add(track)
    }

    // очистка истории
    fun clearHistory() {
        trackList.clear()
    }

    fun saveHistory() {
        val str = createJsonFromTrackList(trackList)
        prefs.edit()
            .putString(PLAYLIST_MAKER_HISTORY, str)
            .apply()
    }

    fun getTracks() : ArrayList<Track> {
        return trackList
    }

    private fun createJsonFromTrackList(facts: ArrayList<Track>): String {
        return Gson().toJson(facts)
    }

    private fun createTracksListFromJson(json: String): ArrayList<Track> {
        return Gson().fromJson(json, Array<Track>::class.java).toCollection(ArrayList())
    }
}