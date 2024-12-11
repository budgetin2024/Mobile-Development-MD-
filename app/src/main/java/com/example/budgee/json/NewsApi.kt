package com.example.budgee.json

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface NewsApi {

    @GET("news") // Pastikan path ini sesuai dengan server Anda
    fun getNews(
        @Header("Authorization") authHeader: String // Menambahkan header Authorization
    ): Call<NewsResponse>
}
