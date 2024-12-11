package com.example.budgee.json

data class Goal(
    val userId: String,
    val goalName: String,
    val goalAmount: Double,
    val currentAmount: Double = 0.0,
    val deadline: String
)
