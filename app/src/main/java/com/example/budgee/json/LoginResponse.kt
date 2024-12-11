package com.example.budgee.json

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null
)
