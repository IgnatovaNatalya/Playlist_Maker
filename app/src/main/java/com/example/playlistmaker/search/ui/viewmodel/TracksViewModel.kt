package com.example.playlistmaker.search.ui.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.HistoryInteractor
import com.example.playlistmaker.search.domain.SearchTracksInteractor
import com.example.playlistmaker.search.domain.model.SearchResult
import com.example.playlistmaker.search.domain.model.Track

class TracksViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TracksViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val tracksInteractor: SearchTracksInteractor = Creator.provideSearchTracksInteractor()
    private val historyInteractor: HistoryInteractor =
        Creator.provideHistoryInteractor(getApplication())

    private val _historyTracks = MutableLiveData<List<Track>>()
    val historyTracks: LiveData<List<Track>> = _historyTracks

    private val _searchTracks = MutableLiveData<List<Track>>()
    val searchTracks: LiveData<List<Track>> = _searchTracks

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private val handler = Handler(Looper.getMainLooper())

    private var latestQueryText: String? = null

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun getSavedHistory() {
        _historyTracks.postValue(
            historyInteractor.getSavedHistory()
        )
    }

    fun historyIsNotEmpty(): Boolean {
        return _historyTracks.value?.isNotEmpty() ?: false
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        _historyTracks.postValue(listOf())
    }

    fun searchDebounce(changedText: String) { //дебаунс в поисковой строке

        //if (latestQueryText == changedText) return

        latestQueryText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchTracks(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    fun searchTracks(queryText: String) {
        if (queryText.isNotEmpty()) {
            renderState(SearchState.Loading)
            tracksInteractor.searchTracks(
                queryText,
                object : SearchTracksInteractor.TracksConsumer {
                    override fun consume(searchResult: SearchResult) {
                        when (searchResult.resultCode) {
                            200 -> {
                                if (searchResult.results.isNotEmpty()) {
                                    _searchTracks.postValue(searchResult.results)
                                    renderState(SearchState.Content)
                                } else
                                    renderState(SearchState.Empty)
                            }
                            else -> { renderState(SearchState.Error)}
                        }
                    }
                }
            )
        }
    }

    private fun renderState(state: SearchState) {
        _searchState.postValue(state)
    }

    fun saveHistory() {
        historyInteractor.saveHistory()
    }

    fun addTrackToHistory(track: Track) {
        historyInteractor.addTrackToHistory(track)
    }


}