package com.example.playlistmaker.sharing.domain

import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun sendLink(link:String)
    fun sendEmail(email: EmailData)
    fun openLink(link:String)
    fun getTermsLink():String
    fun getShareAppLink(): String
    fun getSupportEmailData(): EmailData
}