package com.example.budgee.json

import com.example.budgee.json.TransactionRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionsApi {
    @POST("/transactions")
    fun addTransaction(@Body transaction: TransactionRequest): Call<Void>
}