package com.example.playlistmaker.domain.model

import com.example.playlistmaker.data.db.entity.PlaylistEntity

data class Playlist(
    val id: Int,
    val title: String,
    val description:String,
    val path:String,
    val numberTracks:Int,
    val tracks: List<Track>
) {
    fun toPlaylistEntity(): PlaylistEntity {
        return  PlaylistEntity(
            id,title,description,path,numberTracks
        )
    }
}
