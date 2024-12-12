package com.example.budgee.api

import com.example.budgee.json.TransactionRequest
import com.example.budgee.json.TransactionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TransactionsApi {
    @POST("/transactions")
    fun addTransaction(@Body transaction: TransactionRequest): Call<Void>


    @GET("/transactions")
    fun getTransactions(
        @Query("type") type: String, // Filter berdasarkan tipe (income atau outcome)
        @Query("category") category: String? = null, // Optional filter berdasarkan kategori
        @Query("date") date: String? = null // Optional filter berdasarkan tanggal
    ): Call<List<TransactionResponse>>
}