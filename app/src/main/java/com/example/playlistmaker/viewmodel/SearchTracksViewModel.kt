package com.example.playlistmaker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.search.SearchTracksInteractor
import com.example.playlistmaker.domain.history.HistoryInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.SearchState
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class SearchTracksViewModel(
    private val searchInteractor: SearchTracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private var latestQueryText: String? = null

    private val trackSearchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { request -> searchTracks(request) }

    fun searchDebounce(changedText: String) { //дебаунс в поисковой строке
        if (latestQueryText == changedText) return
        latestQueryText = changedText
        trackSearchDebounce(changedText)
    }

   fun searchTracks(queryText: String) {
        if (queryText.isNotEmpty()) {
            renderState(SearchState.Loading)
            viewModelScope.launch {
                searchInteractor.searchTracks(queryText).collect { result ->
                    when (result.resultCode) {
                        200 -> {
                            if (result.results.isEmpty())
                                renderState(SearchState.Empty)
                            else
                                renderState(SearchState.SearchContent(searchTracks = result.results))
                            }

                        else -> {
                            renderState(SearchState.Error)
                        }
                    }
                }
            }
        }
    }

    fun getSavedHistory() {
        historyInteractor.getSavedHistory()
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