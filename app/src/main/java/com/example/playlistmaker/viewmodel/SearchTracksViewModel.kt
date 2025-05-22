package com.example.playlistmaker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.search.SearchTracksInteractor
import com.example.playlistmaker.domain.history.HistoryInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.SearchState
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class SearchTracksViewModel(
    private val searchInteractor: SearchTracksInteractor,
    private val historyInteractor: HistoryInteractor,
    private val favoritesInteractor: FavoritesInteractor
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
                            if (result.results.isEmpty()) renderState(SearchState.Empty)
                            else renderState(SearchState.SearchContent(searchTracks = result.results))
                        }

                        else -> {
                            renderState(SearchState.Error)
                        }
                    }
                }
            }
        }
    }


    private fun renderState(state: SearchState) {
        _searchState.postValue(state)
    }

    fun addTrackToHistory(track: Track) {
        if (_searchState.value is SearchState.SearchContent)
            viewModelScope.launch { historyInteractor.addTrackToHistory(track) }
    }

    fun onSearchTextChanged(queryText: String) {
        if (queryText.isEmpty())
            showHistory()
        else
            searchDebounce(queryText)
    }

    fun onClickHistoryClearButton() {
        viewModelScope.launch { historyInteractor.clearHistory() }
        _searchState.postValue(SearchState.SearchContent(searchTracks = listOf()))
    }

    fun showHistory() {
        viewModelScope.launch {
            historyInteractor.getHistory().collect { historyTracks ->
                if (historyTracks.isNotEmpty())
                    _searchState.postValue(SearchState.HistoryContent(historyTracks = historyTracks))
                else
                    _searchState.postValue(SearchState.SearchContent(searchTracks = listOf()))
            }
        }
    }

    fun refreshContent() {
        if (_searchState.value is SearchState.SearchContent) {
            val foundTracks = (_searchState.value as SearchState.SearchContent).searchTracks

            viewModelScope.launch {
                favoritesInteractor.getFavoriteTrackIds().collect { favoriteIds ->
                    for (t in foundTracks) if (t.trackId in favoriteIds) t.isFavorite = true
                    _searchState.postValue(SearchState.SearchContent(searchTracks = foundTracks))
                }
            }
        }
        else if(_searchState.value is SearchState.HistoryContent) showHistory()
    }
}