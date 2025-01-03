package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(val clickListener: AdapterClickListener) : RecyclerView.Adapter<TrackViewHolder> () {

    var tracks = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return  TrackViewHolder(v)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(tracks.get(position)) }
    }

    fun interface AdapterClickListener {
        fun onTrackClick(track:Track)
    }
}