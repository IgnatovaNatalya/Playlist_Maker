package com.example.playlistmaker.ui.player

import android.graphics.Bitmap
import android.util.TypedValue
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistLinearBinding
import com.example.playlistmaker.domain.model.Playlist

class PlaylistLinearViewHolder(private val binding: ItemPlaylistLinearBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 2F, itemView.resources.displayMetrics
        )

        Glide.with(itemView)
            .asBitmap()
            .load(playlist.path)
            .centerCrop()
            .placeholder(R.drawable.album_placeholder)
            .into(object : BitmapImageViewTarget(binding.playlistLinearCover) {
                override fun setResource(resource: Bitmap?) {
                    if (resource == null) return
                    val rounded =
                        RoundedBitmapDrawableFactory.create(itemView.resources, resource)
                            .apply { cornerRadius = radiusPx }
                    binding.playlistLinearCover.setImageDrawable(rounded)
                }
            })

        binding.playlistLinearTitle.text = playlist.title

        val str = "${playlist.numTracks} треков"
        binding.playlistLinearNumTracks.text = str

    }
}


