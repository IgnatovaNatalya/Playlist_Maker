package com.example.playlistmaker.domain.player

interface PlaybackInteractor {
    fun preparePlayer(trackUrl:String?, onPrepare: () -> Unit, onComplete: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun isPlaying():Boolean
    fun getCurrentPosition():Int
    fun releasePlayer()
}
