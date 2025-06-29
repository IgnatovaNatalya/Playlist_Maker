package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.FavoritesDao
import com.example.playlistmaker.data.db.dao.HistoryDao
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.dao.TracksPlaylistsDao
import com.example.playlistmaker.data.db.entity.FavTrackEntity
import com.example.playlistmaker.data.db.entity.HistoryEntity
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.db.entity.TracksPlaylistsEntity

@Database(
    version = 3,
    entities = [
        TrackEntity::class,
        FavTrackEntity::class,
        HistoryEntity::class,
        PlaylistEntity::class,
        TracksPlaylistsEntity::class]
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao
    abstract fun historyDao(): HistoryDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun tracksPlaylistsDao(): TracksPlaylistsDao
    abstract fun trackDao(): TrackDao
}