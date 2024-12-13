package com.example.budgee.json

import com.google.gson.annotations.SerializedName

data class GoalsResponse(
    @SerializedName("goals")
    val goals: List<Goal>
)

data class Goal(
    @SerializedName("_id")
    val id: String,
    
    @SerializedName("name")
    val goalName: String,
    
    @SerializedName("targetAmount")
    val targetAmount: Double,
    
    @SerializedName("progress")
    val progress: Double,
    
    @SerializedName("dueDate")
    val duedate: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("userId")
    val userId: String
)
