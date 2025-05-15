package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.ItunesApiService
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.history.HistoryRepository
import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.data.history.HistoryRepositoryImpl
import com.example.playlistmaker.data.search.TrackRepositoryImpl
import com.example.playlistmaker.domain.history.HistoryInteractor
import com.example.playlistmaker.domain.SearchTracksInteractor
import com.example.playlistmaker.domain.search.HistoryInteractorImpl
import com.example.playlistmaker.domain.search.SearchTracksInteractorImpl
import com.example.playlistmaker.search.ui.viewmodel.TracksViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val SearchKoinModule = module {
    factory<HistoryInteractor> { HistoryInteractorImpl(get()) }
    factory<HistoryRepository> { HistoryRepositoryImpl(get(), get()) }

    factory { androidContext().getSharedPreferences("playlist_maker_prefs", Context.MODE_PRIVATE) }
    factory<SearchTracksInteractor> { SearchTracksInteractorImpl(get()) }
    factory<TracksRepository> { TrackRepositoryImpl(get()) }
    factory<NetworkClient> { RetrofitNetworkClient(get(), get()) }

    factory<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    factory { Gson() }

    viewModel { TracksViewModel(get(), get()) }
}
