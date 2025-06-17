package com.example.playlistmaker.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemPlaylistGridBinding
import com.example.playlistmaker.domain.model.Playlist

class PlaylistGridAdapter(private val clickListener: AdapterClickListener) :
    RecyclerView.Adapter<PlaylistGridViewHolder>() {

    var playlists = listOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistGridViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistGridViewHolder(ItemPlaylistGridBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistGridViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlists.get(position)) }
    }

    fun interface AdapterClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}