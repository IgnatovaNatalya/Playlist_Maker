package com.example.playlistmaker.data.sharing

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.util.EmailData
import java.text.SimpleDateFormat
import java.util.Locale

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    private fun shareText(message: String, header: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        val chosenIntent = Intent.createChooser(shareIntent, header)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chosenIntent)
    }

    override fun shareApp() {
        shareText(context.getString(R.string.share_link), context.getString(R.string.share_app))
    }

    override fun sharePlaylist(playlist: Playlist, listTrack: List<Track>) {
        shareText(
            playlistToShare(playlist) + "\n\n" + listTracksToShare(listTrack),
            context.getString(R.string.share_playlist)
        )
    }

    private fun playlistToShare(playlist: Playlist) = listOf(playlist.title)
        .plus(playlist.description.takeIf { it.isNotEmpty() })
        .plus(
            context.resources.getQuantityString(
                R.plurals.tracks,
                playlist.numTracks,
                playlist.numTracks
            )
        )
        .filterNotNull()
        .joinToString("\n")

    private fun listTracksToShare(listTrack: List<Track>): String {
        return listTrack
            .mapIndexed { index, track -> "${index + 1}. ${track.artistName} - ${track.trackName} (${SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)})" }
            .joinToString(separator = "\n")
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

//    override fun getShareAppLink(): String {
//        return context.getString(R.string.share_link)
//    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.support_email),
            subject = context.getString(R.string.support_subject),
            text = context.getString(R.string.support_text)
        )
    }
}