package com.example.playlistmaker.player.ui.viewmodel

sealed interface PlayerState {
    data object Playing:PlayerState
    data object Paused: PlayerState
    data object Prepared:PlayerState
    data object Completed:PlayerState
    data object NotPrepared:PlayerState
}
