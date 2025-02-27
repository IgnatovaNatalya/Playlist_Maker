package com.example.playlistmaker.data.repositoryImpl

import android.content.Context
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PreferencesRepository
import com.google.gson.Gson

const val PLAYLIST_MAKER_PREFS = "playlist_maker_prefs"
const val PLAYLIST_MAKER_HISTORY = "playlist_maker_history"
const val DARK_THEME = "dark_theme"

class PreferencesRepositoryImpl(context: Context) : PreferencesRepository {

    private val gson = Gson()
    private var sharedPrefs = context.getSharedPreferences(PLAYLIST_MAKER_PREFS, Context.MODE_PRIVATE)

    override fun containsTheme(): Boolean {
        return sharedPrefs.contains(DARK_THEME)
    }

    override fun getSavedTheme():Boolean {
        return sharedPrefs.getBoolean(DARK_THEME, false)
    }

    override fun saveTheme(theme: Boolean) {
        sharedPrefs.edit()
            .putBoolean(DARK_THEME, theme)
            .apply()
    }

    override fun getSavedHistory():List<Track> {
        val str = sharedPrefs.getString(PLAYLIST_MAKER_HISTORY, null)
        if (str != null) return createTracksListFromJson(str)
        return listOf()
    }

    override fun saveHistory(trackList:List<Track>) {
        val str = createJsonFromTrackList(trackList)
        sharedPrefs.edit()
            .putString(PLAYLIST_MAKER_HISTORY, str)
            .apply()
    }

    override fun clearHistory() {
        sharedPrefs.edit()
            .remove(PLAYLIST_MAKER_HISTORY)
            .apply()
    }

    private fun createJsonFromTrackList(facts: List<Track>): String {
        return gson.toJson(facts)
    }

    private fun createTracksListFromJson(json: String): List<Track> {
        return gson.fromJson(json,Array<Track>::class.java).toList()
    }
}