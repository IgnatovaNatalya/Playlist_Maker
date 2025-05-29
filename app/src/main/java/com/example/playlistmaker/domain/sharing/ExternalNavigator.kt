package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.util.EmailData

interface ExternalNavigator {
    fun sendLink(link:String)
    fun sendEmail(email: EmailData)
    fun openLink(link:String)
    fun getTermsLink():String
    fun getShareAppLink(): String
    fun getSupportEmailData(): EmailData
}