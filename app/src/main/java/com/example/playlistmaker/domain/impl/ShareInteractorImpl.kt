package com.example.playlistmaker.domain.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.interactor.ShareInteractor

class ShareInteractorImpl(private val context: Context) : ShareInteractor {
    override fun sendLink() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_link))
        val chosenIntent = Intent.createChooser(shareIntent, context.getString(R.string.share_app))
        context.startActivity(chosenIntent)
    }

    override fun sendEmail() {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(context.getString(R.string.support_email))
            )
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_text))
        }
        context.startActivity(supportIntent)
    }

    override fun openLink() {
        val agreementIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.agreement_link)))
        context.startActivity(agreementIntent)
    }
}