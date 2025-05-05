package com.example.newsapp

import android.net.http.UrlRequest.Status

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>

)

data class Article(
    val title: String,
    val description: String?,
    val urlToImage: String?,
    val url: String,
    val content: String?
)
