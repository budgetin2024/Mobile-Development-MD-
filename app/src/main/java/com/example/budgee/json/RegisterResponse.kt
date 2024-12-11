package com.example.budgee.json

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val error: String? = null
)
