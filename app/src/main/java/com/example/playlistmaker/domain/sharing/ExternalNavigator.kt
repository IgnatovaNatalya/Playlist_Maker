package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.EmailData

interface ExternalNavigator {
    fun shareApp()
    fun sharePlaylist(playlist: Playlist, listTrack:List<Track>)
    fun sendEmail(email: EmailData)
    fun openLink(link:String)
    fun getTermsLink():String
    //fun getShareAppLink(): String
    fun getSupportEmailData(): EmailData
}