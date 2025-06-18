package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track

interface SharingInteractor {
    fun shareApp()
    fun sharePlaylist(playlist: Playlist, listTrack:List<Track>)
    fun sendEmail()
    fun openTerms()
}