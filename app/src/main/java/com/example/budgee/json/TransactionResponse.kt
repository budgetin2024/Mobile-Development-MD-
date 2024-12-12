package com.example.budgee.json

data class TransactionResponse(
    val id: String,
    val amount: Double,
    val type: String,
    val category: String,
    val date: String,
    val description: String
)

