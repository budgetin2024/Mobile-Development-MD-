package com.example.budgee.json

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GoalApi {

    // Menambahkan header Authorization dengan token
    @POST("goals")
    fun createGoalWithAuth(
        @Body goal: Goal,
        @Header("Authorization") authToken: String
    ): Call<Goal>

}
