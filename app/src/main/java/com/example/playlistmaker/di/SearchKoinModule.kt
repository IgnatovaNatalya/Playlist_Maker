package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ItunesApiService
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.HistoryRepository
import com.example.playlistmaker.search.data.repository.TracksRepository
import com.example.playlistmaker.search.data.repositoryImpl.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.repositoryImpl.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.HistoryInteractor
import com.example.playlistmaker.search.domain.SearchTracksInteractor
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.SearchTracksInteractorImpl
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
    factory<NetworkClient> { RetrofitNetworkClient(get()) }

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
