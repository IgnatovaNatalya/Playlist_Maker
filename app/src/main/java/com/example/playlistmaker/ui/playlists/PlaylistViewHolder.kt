package com.example.playlistmaker.ui.playlists

import android.graphics.Bitmap
import android.util.TypedValue
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.domain.model.Playlist

class PlaylistViewHolder(private val binding: ItemPlaylistBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {

        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8F, itemView.resources.displayMetrics
        )

        Glide.with(itemView)
            .asBitmap()
            .load(playlist.path)
            .centerCrop()
            .placeholder(R.drawable.album_placeholder)
            .into(object : BitmapImageViewTarget(binding.playlistCover) {
                override fun setResource(resource: Bitmap?) {
                    if (resource == null) return
                    val rounded =
                        RoundedBitmapDrawableFactory.create(itemView.resources, resource)
                            .apply { cornerRadius = radiusPx }
                    binding.playlistCover.setImageDrawable(rounded)
                }
            })

        binding.playlistTitle.text = playlist.title

        val str = "${playlist.numberTracks} треков"
        binding.numTracks.text = str
    }
}


