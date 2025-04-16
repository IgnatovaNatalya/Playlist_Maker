package com.example.playlistmaker.search.ui.fragment

import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.search.domain.model.Track

class TrackViewHolder(private val binding: ItemTrackBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        binding.songTitle.text = track.trackName

        binding.bandName.text = track.artistName

        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 2F, itemView.resources.displayMetrics
        ).toInt()

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerInside()
            .transform(RoundedCorners(radiusPx))
            .placeholder(R.drawable.album_placeholder)
            .into(binding.albumPicture)

        binding.songDuration.text = track.duration
    }
}


