package com.example.budgee.json

data class TransactionsResponse(
    val transactions: List<TransactionResponse>
)

data class TransactionResponse(
    val _id: String,
    val userId: String,
    val type: String,
    val category: String,
    val amount: Double,
    val date: String
)