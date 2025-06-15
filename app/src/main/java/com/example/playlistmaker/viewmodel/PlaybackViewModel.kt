package com.example.playlistmaker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.player.PlaybackInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.util.AddToPlaylistResult
import com.example.playlistmaker.util.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.playlistmaker.util.PlayerUiState

class PlaybackViewModel(
    private val playbackInteractor: PlaybackInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private var playlistsInteractor: PlaylistsInteractor,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _toastState = MutableLiveData<String>()
    val toastState: LiveData<String> = _toastState

    private val _currentTrack = savedStateHandle.getLiveData<Track>(TRACK_KEY)
    val currentTrack: LiveData<Track?> = _currentTrack

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite


    val playerUiState = MediatorLiveData<PlayerUiState>().apply {
        var state: PlayerState? = null
        var isFav: Boolean? = null

        fun update() {
            state?.let {
                postValue(
                    PlayerUiState(
                        it.isPlayButtonEnabled,
                        it.buttonResource,
                        it.progress,
                        isFav == true
                    )
                )
            }
        }
        addSource(playerState) { state = it; update() }
        addSource(isFavorite) { isFav = it; update() }
    }

    private var timerJob: Job? = null

    init {
        getPlaylists()
    }

    fun setTrack(track: Track) {
        _currentTrack.postValue(track)
        _playerState.postValue(PlayerState.Default())
        _isFavorite.postValue(track.isFavorite)
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                _playlists.postValue(playlists)
            }
        }
    }

    private fun startPlayer() {
        val track = currentTrack.value
        if (track != null) {
            playbackInteractor.startPlayer()
            _playerState.postValue(
                PlayerState.Playing(getCurrentPlayerPosition())
            )
            startTimer()
        }
    }

    private fun pausePlayer() {
        val track = currentTrack.value
        if (track != null) {
            playbackInteractor.pausePlayer()
            timerJob?.cancel()
            _playerState.postValue(
                PlayerState.Paused(getCurrentPlayerPosition())
            )
        }
    }

    fun releasePlayer() {
        val track = currentTrack.value
        if (track != null) {
            playbackInteractor.releasePlayer()
            _playerState.postValue(PlayerState.Default())
        }
    }

    fun preparePlayer(track: Track) {
        playbackInteractor.preparePlayer(
            track.previewUrl,
            { _playerState.postValue(PlayerState.Prepared()) },
            { _playerState.postValue(PlayerState.Prepared()) }
        )
    }

    fun onLikeClicked() {
        val track = currentTrack.value
        if (track != null) {
            viewModelScope.launch {
                if (track.isFavorite == true)
                    favoritesInteractor.removeFromFavorites(track)
                else
                    favoritesInteractor.addToFavorites(track)
            }
            _currentTrack.postValue(track.copy(isFavorite = !track.isFavorite))
            _isFavorite.postValue(!track.isFavorite)
        }

    }

    fun onPlayButtonClicked() {
        when (_playerState.value) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> {}
        }
    }

    fun addTrackTo(playList: Playlist) {
        val currentTrack = _currentTrack.value
        if (currentTrack != null) {
            viewModelScope.launch {
                when (val result = playlistsInteractor.addToPlaylist(playList.id, currentTrack)) {
                    is AddToPlaylistResult.Success -> {
                        _toastState.postValue("Добавлено в плейлист ${playList.title}")
                    }

                    is AddToPlaylistResult.AlreadyExists -> {
                        _toastState.postValue("Трек уже добавлен в плейлист ${playList.title}")
                    }

                    is AddToPlaylistResult.Error -> {
                        _toastState.postValue("Ошибка: ${result.exception.message}")
                    }
                }
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (playbackInteractor.isPlaying()) {
                _playerState.postValue(
                    PlayerState.Playing(getCurrentPlayerPosition())
                )
                delay(TIMER_UPDATE_INTERVAL)
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(playbackInteractor.getCurrentPosition()) ?: "00:00"
    }

    private fun clearToast() {
        _toastState.postValue("")
    }

    fun onPause() {
        pausePlayer()
        clearToast()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    companion object {
        const val TIMER_UPDATE_INTERVAL = 300L
        const val TRACK_KEY = "TRACK_KEY"
    }
}






