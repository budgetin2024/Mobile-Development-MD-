package com.example.budgee.json

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // BASE URL untuk API Registrasi
    private const val AUTH_API_BASE_URL = "https://backend-budgetin.et.r.appspot.com/"

    private const val GOALS_API_BASE_URL = "https://backend-budgetin.et.r.appspot.com/"

    // Retrofit instance untuk Authentication API
    val authRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(AUTH_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val goalsRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(GOALS_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}