package com.example.budgee.json

data class TransactionItem(
    val _id: String,
    val userId: String,
    val type: String,
    val category: String,
    val amount: Int,
    val date: String,
)
