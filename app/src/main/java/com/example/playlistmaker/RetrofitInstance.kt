package com.example.playlistmaker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://itunes.apple.com"

    val apiService: iTunesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            //.client(okHttpClient)
            .build()
            .create(iTunesApi::class.java)
    }
}