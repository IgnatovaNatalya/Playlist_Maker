package com.example.playlistmaker

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val albumPicture: ImageView = itemView.findViewById(R.id.album_picture)
    private val songTitle: TextView = itemView.findViewById(R.id.song_title)
    private val songDuration: TextView = itemView.findViewById(R.id.song_duration)
    private val bandName: TextView = itemView.findViewById(R.id.band_name)

    fun bind(track: Track) {
        songTitle.text = track.trackName
        songDuration.text = track.trackTime
        bandName.text = track.artistName

        val radiusDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 2F, itemView.resources.displayMetrics).toInt()

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerInside()
            .transform(RoundedCorners(radiusDp))
            .placeholder(R.drawable.album_placeholder)
            .into(albumPicture)
    }
}


