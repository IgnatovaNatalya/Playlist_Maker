package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.db.dao.FavoritesDao
import com.example.playlistmaker.data.db.dao.HistoryDao

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao
    abstract fun historyDao(): HistoryDao
}