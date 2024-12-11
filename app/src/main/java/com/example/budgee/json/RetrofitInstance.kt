package com.example.budgee.json

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // BASE URL untuk API Registrasi
    private const val AUTH_API_BASE_URL = "https://backend-budgetin.et.r.appspot.com/"
    // BASE URL untuk News API
    private const val NEWS_API_BASE_URL = "https://backend-budgetin.et.r.appspot.com/"

    // Client with Logging Interceptor
    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    // Retrofit instance untuk Authentication API
    val authRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(AUTH_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val api: NewsApi by lazy {
        Retrofit.Builder()
            .baseUrl(NEWS_API_BASE_URL) // Pastikan base URL sesuai dengan server API Anda
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(NewsApi::class.java)
    }
}
