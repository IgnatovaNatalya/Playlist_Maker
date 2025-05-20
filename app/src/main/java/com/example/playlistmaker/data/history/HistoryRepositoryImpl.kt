package com.example.playlistmaker.data.history


import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.toListDomainModel
import com.example.playlistmaker.domain.history.HistoryRepository
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.model.toHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class HistoryRepositoryImpl(
//    private val sharedPrefs: SharedPreferences,
//    private val gson: Gson
    private val appDatabase: AppDatabase
) :
    HistoryRepository {

    override fun getHistory():  Flow<List<Track>> = flow {
        val historyTracks = appDatabase.historyDao().getHistoryTracks()
        emit(toListDomainModel(historyTracks))
    }

    override suspend fun addToHistory(track: Track) {
        val historyEntity = track.toHistoryEntity(track)
        appDatabase.historyDao().addToHistory(historyEntity)
    }


    override suspend fun clearHistory() {
        appDatabase.historyDao().clearHistory()
    }

    //    companion object {
//        const val PLAYLIST_MAKER_HISTORY = "playlist_maker_history"
//    }
//
//    override fun getSavedHistory(): List<Track> {
//        val str = sharedPrefs.getString(PLAYLIST_MAKER_HISTORY, null)
//        if (str != null) return createTracksListFromJson(str)
//        return listOf()
//    }
//
//    override fun saveHistory(trackList: List<Track>) {
//        val str = createJsonFromTrackList(trackList)
//        sharedPrefs.edit {
//            putString(PLAYLIST_MAKER_HISTORY, str)
//        }
//    }
//
//    override fun clearHistory() {
//        sharedPrefs.edit {
//            remove(PLAYLIST_MAKER_HISTORY)
//        }
//    }
//
//    private fun createJsonFromTrackList(facts: List<Track>): String {
//        return gson.toJson(facts)
//    }
//
//    private fun createTracksListFromJson(json: String): List<Track> {
//        return gson.fromJson(json, Array<Track>::class.java).toList()
//    }

}