package com.example.budgee.json

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)