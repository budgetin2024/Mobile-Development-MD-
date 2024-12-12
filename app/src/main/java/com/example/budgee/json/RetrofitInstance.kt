package com.example.budgee.json

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // BASE URL untuk API Registrasi
    private const val AUTH_API_BASE_URL = "https://backend-budgetin.et.r.appspot.com/"
    // BASE URL untuk News API
    private const val NEWS_API_BASE_URL = "https://backend-budgetin.et.r.appspot.com/"

    private const val GOALS_API_BASE_URL = "https://backend-budgetin.et.r.appspot.com/"

    // Retrofit instance untuk Authentication API
    val authRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(AUTH_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: NewsApi by lazy {
        Retrofit.Builder()
            .baseUrl(NEWS_API_BASE_URL) // Pastikan base URL sesuai dengan server API Anda
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }

    val goalsRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(GOALS_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}