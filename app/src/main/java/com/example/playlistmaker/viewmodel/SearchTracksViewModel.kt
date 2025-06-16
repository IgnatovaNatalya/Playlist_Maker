package com.example.playlistmaker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.search.SearchTracksInteractor
import com.example.playlistmaker.domain.history.HistoryInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.SearchState
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchTracksViewModel(
    private val searchInteractor: SearchTracksInteractor,
    private val historyInteractor: HistoryInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    // Постоянное хранение данных
    private val _historyTracks = MutableStateFlow<List<Track>>(listOf())

    // Текущее состояние UI
    private val _uiState = MutableStateFlow<SearchState>(SearchState.Empty)
    val uiState = _uiState.asStateFlow()

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
            _uiState.value = SearchState.Loading

            viewModelScope.launch {
                searchInteractor.searchTracks(queryText).collect { result ->
                    when (result.resultCode) {

                        200 -> {
                            if (result.results.isEmpty()) _uiState.value = SearchState.NotFound
                            else {
                                _uiState.value = SearchState.SearchContent(result.results.sortedByDescending { it.isFavorite })
                            }
                        }

                        else -> {
                            _uiState.value = SearchState.Error
                        }
                    }
                }
            }
        }
    }

    fun addTrackToHistory(track: Track) {
        if (_uiState.value is SearchState.SearchContent)
            viewModelScope.launch { historyInteractor.addTrackToHistory(track) }
    }

    fun onSearchTextChanged(queryText: String) {
        if (queryText.isEmpty())
            onClearSearchClicked()
        else {
            searchDebounce(queryText)
        }
    }

    fun onClickHistoryClearButton() {
        viewModelScope.launch { historyInteractor.clearHistory() }
        _uiState.value = SearchState.Empty
    }

    private fun getHistory() {
        viewModelScope.launch {
            historyInteractor.getHistory().collect { historyTracks ->
                _historyTracks.value = historyTracks
            }
        }
    }

    fun onClearSearchClicked() {
        if (_historyTracks.value.isEmpty())
            _uiState.value = SearchState.Empty
        else
            _uiState.value = SearchState.HistoryContent(_historyTracks.value)
    }

    fun refreshContent() {
        if (_uiState.value is SearchState.SearchContent) {
            viewModelScope.launch {
                favoritesInteractor.getFavoriteTrackIds().collect { favoriteIds ->
                    for (t in (_uiState.value as SearchState.SearchContent).foundTracks) if (t.trackId in favoriteIds) t.isFavorite = true
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}