package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.db.dao.FavoritesDao
import com.example.playlistmaker.data.db.dao.HistoryDao
import com.example.playlistmaker.data.db.entity.HistoryEntity
import com.example.playlistmaker.data.db.entity.PlaylistEntity

@Database(version = 2, entities = [TrackEntity::class, HistoryEntity::class, PlaylistEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao
    abstract fun historyDao(): HistoryDao
    abstract fun playlistDao(): PlaylistDao
}