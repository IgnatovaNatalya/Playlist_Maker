package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareApp()
    }
    override fun sharePlaylist(playlist: Playlist, listTrack:List<Track>) {
        externalNavigator.sharePlaylist(playlist,listTrack)
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun sendEmail() {
        externalNavigator.sendEmail(getSupportEmailData())
    }

    private fun getSupportEmailData(): EmailData {
        return externalNavigator.getSupportEmailData()
    }

    private fun getTermsLink(): String {
        return externalNavigator.getTermsLink()
    }


}