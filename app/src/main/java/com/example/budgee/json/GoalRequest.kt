package com.example.budgee.json

import com.google.gson.annotations.SerializedName

data class GoalRequest(
    @SerializedName("goalName")
    val goalName: String,

    @SerializedName("goalAmount")
    val goalAmount: Double,

    @SerializedName("currentAmount")
    val currentAmount: Double = 0.0,

    @SerializedName("deadline")
    val deadline: String,

    @SerializedName("category")
    val category: String
)