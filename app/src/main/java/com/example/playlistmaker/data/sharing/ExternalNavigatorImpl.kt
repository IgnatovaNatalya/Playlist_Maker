package com.example.playlistmaker.data.sharing

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.util.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun sendLink(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        val chosenIntent = Intent.createChooser(shareIntent, context.getString(R.string.share_app))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chosenIntent)
    }

    override fun sendEmail(email: EmailData) {

        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email.email))
            putExtra(Intent.EXTRA_SUBJECT, email.subject)
            putExtra(Intent.EXTRA_TEXT, email.text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(supportIntent)
    }

    override fun openLink(link: String) {
        val agreementIntent =
            Intent(Intent.ACTION_VIEW, link.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(agreementIntent)
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.agreement_link)
    }

    override fun getShareAppLink(): String {
        return context.getString(R.string.share_link)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.support_email),
            subject = context.getString(R.string.support_subject),
            text = context.getString(R.string.support_text)
        )
    }
}