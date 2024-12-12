package com.example.budgee.json

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("user") val user: User
)

data class User(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String
)