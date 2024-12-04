package com.example.budgee.json

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("top-headlines")
    fun getTopHeadlines(
        @Query("country") country: String = "id",
        @Query("apiKey") apiKey: String = "d785abb609e845ffa5828f81a90faf18"
    ): Call<NewsResponse>
}
