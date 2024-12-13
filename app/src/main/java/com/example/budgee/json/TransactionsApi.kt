package com.example.budgee.json

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface TransactionsApi {
    @GET("/transactions")
    fun getTransactions(
        @Query("type") type: String? = null
    ): Call<TransactionsResponse>

    @POST("/transactions")
    fun addTransaction(@Body transaction: TransactionRequest): Call<TransactionResponse>

}