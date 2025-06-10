package com.example.playlistmaker.ui.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemPlaylistLinearBinding
import com.example.playlistmaker.domain.model.Playlist

class PlaylistLinearAdapter(private val clickListener: AdapterClickListener) :
    RecyclerView.Adapter<PlaylistLinearViewHolder>() {

    var playlists = listOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistLinearViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistLinearViewHolder(ItemPlaylistLinearBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistLinearViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlists.get(position)) }
    }

    fun interface AdapterClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}