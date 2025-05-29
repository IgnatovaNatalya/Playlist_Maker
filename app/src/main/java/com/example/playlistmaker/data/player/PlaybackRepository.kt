package com.example.playlistmaker.data.player

interface PlaybackRepository {
    fun preparePlayer(trackUrl: String?, onPrepare: () -> Unit, onComplete: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun releasePlayer()
}