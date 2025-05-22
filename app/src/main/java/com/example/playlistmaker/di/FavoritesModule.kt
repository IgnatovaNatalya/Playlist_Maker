package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.favorites.FavoritesRepositoryImpl
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.favorites.FavoritesInteractorImpl
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val favoritesModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }
    single<FavoritesInteractor> { FavoritesInteractorImpl(get()) }
}