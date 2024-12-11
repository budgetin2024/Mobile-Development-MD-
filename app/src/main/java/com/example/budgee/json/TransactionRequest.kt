package com.example.budgee.json

data class TransactionRequest(
    val amount: Double,
    val category: String,
    val date: String,
    val type: String,
    val description: String
)