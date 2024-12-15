package com.example.budgee.json

data class NewsResponse(
	val status: String,
	val totalResults: Int,
	val articles: List<Article>
)

data class Source(
	val id: String?,
	val name: String
)


