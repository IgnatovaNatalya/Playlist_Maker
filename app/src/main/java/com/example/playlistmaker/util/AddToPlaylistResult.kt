package com.example.playlistmaker.util

sealed class AddToPlaylistResult {
    object Success : AddToPlaylistResult()
    object AlreadyExists : AddToPlaylistResult()
    data class Error(val exception: Throwable) : AddToPlaylistResult()
}