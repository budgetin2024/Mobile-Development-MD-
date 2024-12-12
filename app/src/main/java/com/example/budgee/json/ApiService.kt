package com.example.budgee.json

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {

    @GET("auth/user")
    fun getUserData(@Header("Authorization") token: String): Call<UserData>
}