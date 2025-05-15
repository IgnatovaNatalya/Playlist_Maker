package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.ItunesApiService
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.history.HistoryRepository
import com.example.playlistmaker.data.search.SearchTracksRepository
import com.example.playlistmaker.data.history.HistoryRepositoryImpl
import com.example.playlistmaker.data.search.SearchTrackRepositoryImpl
import com.example.playlistmaker.domain.history.HistoryInteractor
import com.example.playlistmaker.domain.search.SearchTracksInteractor
import com.example.playlistmaker.domain.history.HistoryInteractorImpl
import com.example.playlistmaker.domain.search.SearchTracksInteractorImpl
import com.example.playlistmaker.viewmodel.SearchTracksViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchTracksModule = module {
    factory<HistoryInteractor> { HistoryInteractorImpl(get()) }
    factory<HistoryRepository> { HistoryRepositoryImpl(get(), get()) }

    factory { androidContext().getSharedPreferences("playlist_maker_prefs", Context.MODE_PRIVATE) }
    factory<SearchTracksInteractor> { SearchTracksInteractorImpl(get()) }
    factory<SearchTracksRepository> { SearchTrackRepositoryImpl(get()) }
    factory<NetworkClient> { RetrofitNetworkClient(get(), get()) }

    factory<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    factory { Gson() }

    viewModel { SearchTracksViewModel(get(), get()) }
}
