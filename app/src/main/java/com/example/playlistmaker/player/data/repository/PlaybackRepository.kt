package com.example.playlistmaker.player.data.repository

interface PlaybackRepository {
    fun preparePlayer(trackUrl: String?, onPrepare: () -> Unit, onComplete: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    //fun playbackControl()
    fun releasePlayer()
}