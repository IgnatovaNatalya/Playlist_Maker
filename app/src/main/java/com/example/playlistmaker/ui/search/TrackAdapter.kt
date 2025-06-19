package com.example.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.TrackDiffCallback

class TrackAdapter(
    private val clickListener: AdapterClickListener,
    private val longClickListener: AdapterLongClickListener? = null
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = listOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(ItemTrackBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(tracks[position]) }
        if (longClickListener != null)
            holder.itemView.setOnLongClickListener {
                longClickListener.onTrackLongClick(tracks[position])
            }
    }

    fun updateTracks(newTracks: List<Track>) {
        val diffResult = DiffUtil.calculateDiff(TrackDiffCallback(tracks, newTracks))
        tracks = newTracks
        diffResult.dispatchUpdatesTo(this)
    }

    fun interface AdapterClickListener {
        fun onTrackClick(track: Track)
    }

    fun interface AdapterLongClickListener {
        fun onTrackLongClick(track: Track):Boolean
    }
}