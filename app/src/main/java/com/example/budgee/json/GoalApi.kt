package com.example.budgee.json

import retrofit2.Call
import retrofit2.http.*

interface GoalApi {
    @GET("goals")
    fun getGoals(@Header("Authorization") token: String): Call<GoalsResponse>

    @POST("goals")
    fun createGoalWithAuth(
        @Body goalRequest: GoalRequest,
        @Header("Authorization") token: String
    ): Call<Goal>

    @DELETE("goals/delete/{id}")
    fun deleteGoal(
        @Path("id") goalId: String,
        @Header("Authorization") token: String
    ): Call<Void>

    @PUT("goals/{id}")
    fun updateGoal(
        @Path("id") goalId: String,
        @Body goalRequest: GoalRequest,
        @Header("Authorization") token: String
    ): Call<Goal>

    @PUT("goals/{id}/progress")
    fun updateGoalProgress(
        @Path("id") goalId: String,
        @Body progressRequest: ProgressRequest,
        @Header("Authorization") token: String
    ): Call<Goal>
}

data class ProgressRequest(
    val progress: Double
)

