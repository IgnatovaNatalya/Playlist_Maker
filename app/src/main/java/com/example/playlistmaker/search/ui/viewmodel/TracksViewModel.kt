package com.example.playlistmaker.search.ui.viewmodel

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.HistoryInteractor
import com.example.playlistmaker.search.domain.SearchTracksInteractor
import com.example.playlistmaker.search.domain.model.SearchResult
import com.example.playlistmaker.search.domain.model.Track

class TracksViewModel(
    private val searchInteractor: SearchTracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private val handler = Handler(Looper.getMainLooper())

    private var latestQueryText: String? = null

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun getSavedHistory() {
        historyInteractor.getSavedHistory()
    }

    fun searchDebounce(changedText: String) { //дебаунс в поисковой строке

        if (latestQueryText == changedText) return

        latestQueryText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchTracks(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    fun searchTracks(queryText: String) {
        if (queryText.isNotEmpty()) {
            renderState(SearchState.Loading)
            searchInteractor.searchTracks(
                queryText,
                object : SearchTracksInteractor.TracksConsumer {
                    override fun consume(searchResult: SearchResult) {
                        when (searchResult.resultCode) {
                            200 -> {
                                if (searchResult.results.isEmpty())
                                    renderState(SearchState.Empty)
                                else
                                    renderState(SearchState.SearchContent(searchTracks = searchResult.results))
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
        if (_searchState.value is SearchState.SearchContent)
            historyInteractor.addTrackToHistory(track)
    }

    fun onSearchTextChanged(queryText: String) {
        if (queryText.isEmpty())
            _searchState.postValue(SearchState.HistoryContent(historyTracks = historyInteractor.getTracks()))
        else
            searchDebounce(queryText)
    }

    fun onClickHistoryClearButton() {
        historyInteractor.clearHistory()
        _searchState.postValue(SearchState.SearchContent(searchTracks = listOf()))
    }

    fun showHistory() {
        if (historyInteractor.getTracks().isNotEmpty())
            _searchState.postValue(SearchState.HistoryContent(historyTracks = historyInteractor.getTracks()))
        else
            _searchState.postValue(SearchState.SearchContent(searchTracks = listOf()))
    }
}