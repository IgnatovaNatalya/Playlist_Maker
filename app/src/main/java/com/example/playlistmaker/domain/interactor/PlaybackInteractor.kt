package com.example.playlistmaker.domain.interactor

interface PlaybackInteractor {
    fun preparePlayer(trackUrl:String?, onPrepare: OnPreparedListener, onComplete:OnCompletionListener )
    fun startPlayer()
    fun pausePlayer()
    fun isPlaying():Boolean
    fun playbackControl()
    fun releasePlayer()

    fun interface OnPreparedListener{
        fun onPrepare()
    }
    fun interface OnCompletionListener {
        fun onComplete()
    }
}
