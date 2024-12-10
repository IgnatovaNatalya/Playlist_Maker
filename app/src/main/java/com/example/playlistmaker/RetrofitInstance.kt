package com.example.playlistmaker

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitInstance {

    private const val BASE_URL = "https://itunes.apple.com"

//    val okHttpClient = OkHttpClient.Builder()
//        .readTimeout(30, TimeUnit.MILLISECONDS)
//        .connectTimeout(30, TimeUnit.MILLISECONDS)
//        .build()

    val apiService: iTunesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            //.client(okHttpClient)
            .build()
            .create(iTunesApi::class.java)
    }
}