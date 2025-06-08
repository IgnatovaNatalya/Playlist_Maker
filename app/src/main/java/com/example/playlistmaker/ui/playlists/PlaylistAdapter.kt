package com.example.playlistmaker.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.domain.model.Playlist

class PlaylistAdapter(private val clickListener: AdapterClickListener) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    var playlists = listOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(ItemPlaylistBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlists.get(position)) }
    }

    fun interface AdapterClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}