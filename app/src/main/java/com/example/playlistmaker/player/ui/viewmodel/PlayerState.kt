package com.example.playlistmaker.player.ui.viewmodel

import com.example.playlistmaker.R


//sealed interface PlayerState {
//
//    data class Playing(val playerTime: Int) : PlayerState
//
//    data object Paused : PlayerState
//    data object Prepared:PlayerState
//    data object Completed:PlayerState
//    data object NotPrepared:PlayerState
//}


sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val buttonResource: Int,
    val progress: String
) {

    class Default : PlayerState(false, R.drawable.button_play, "00:00")
    class Prepared : PlayerState(true, R.drawable.button_play, "00:00")
    class Playing(progress: String) : PlayerState(true, R.drawable.button_pause, progress)
    class Paused(progress: String) : PlayerState(true, R.drawable.button_play, progress)
}