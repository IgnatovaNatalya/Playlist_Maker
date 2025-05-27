package com.example.playlistmaker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.search.SearchTracksInteractor
import com.example.playlistmaker.domain.history.HistoryInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.HistoryState
import com.example.playlistmaker.util.SearchState
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchTracksViewModel(
    private val searchInteractor: SearchTracksInteractor,
    private val historyInteractor: HistoryInteractor,
    //private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Empty)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _historyState = MutableStateFlow<HistoryState>(HistoryState.Empty)
    val historyState: StateFlow<HistoryState> = _historyState.asStateFlow()

    private val _viewModelState =
        MutableStateFlow<SearchViewModelState>(SearchViewModelState.Search)
    val viewModeState: StateFlow<SearchViewModelState> = _viewModelState.asStateFlow()

    private var latestQueryText: String? = null

    private val trackSearchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { request -> searchTracks(request) }

    init {
        getHistory()
    }

    fun searchDebounce(changedText: String) { //дебаунс в поисковой строке
        if (latestQueryText == changedText) return
        latestQueryText = changedText
        trackSearchDebounce(changedText)
    }

    fun searchTracks(queryText: String) {
        if (queryText.isNotEmpty()) {
            _searchState.value = SearchState.Loading

            viewModelScope.launch {
                searchInteractor.searchTracks(queryText).collect { result ->
                    when (result.resultCode) {

                        200 -> {
                            if (result.results.isEmpty()) _searchState.value = SearchState.Empty
                            else _searchState.value =
                                SearchState.SearchContent(searchTracks = result.results)
                        }

                        else -> {
                            _searchState.value = SearchState.Error
                        }
                    }
                }
            }
        }
    }

    fun addTrackToHistory(track: Track) {
        if (_searchState.value is SearchState.SearchContent)
            viewModelScope.launch { historyInteractor.addTrackToHistory(track) }
    }

    fun onSearchTextChanged(queryText: String) {
        if (queryText.isEmpty())
            _viewModelState.value = SearchViewModelState.History
        else {
            _viewModelState.value = SearchViewModelState.Search
            searchDebounce(queryText)
        }
    }

    fun onClickHistoryClearButton() {
        viewModelScope.launch { historyInteractor.clearHistory() }
        _searchState.value = SearchState.Empty //SearchContent(searchTracks = listOf())
    }

    fun getHistory() {
        viewModelScope.launch {
            historyInteractor.getHistory().collect { historyTracks ->
                if (historyTracks.isNotEmpty())
                    _historyState.value = HistoryState.HistoryContent(historyTracks = historyTracks)
                else
                    _historyState.value = HistoryState.Empty
                // _searchState.postValue(SearchState.SearchContent(searchTracks = listOf()))
            }
        }
    }

//    fun refreshContent() {
//        if (_searchState.value is SearchState.SearchContent) {
//            val foundTracks = (_searchState.value as SearchState.SearchContent).searchTracks
//
//            viewModelScope.launch {
//                favoritesInteractor.getFavoriteTrackIds().collect { favoriteIds ->
//                    for (t in foundTracks) if (t.trackId in favoriteIds) t.isFavorite = true
//                    _searchState.postValue(SearchState.SearchContent(searchTracks = foundTracks))
//                }
//            }
//        } else if (_searchState.value is SearchState.HistoryContent) getHistory()
//    }
}