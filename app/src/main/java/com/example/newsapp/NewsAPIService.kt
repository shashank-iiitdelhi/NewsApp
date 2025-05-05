package com.example.newsapp

import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response

interface NewsAPIService {

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse> // Return Response<NewsResponse>

    @GET("top-headlines")
    suspend fun getTrendingNewsByCategory(
        @Query("category") category: String,
        @Query("apiKey") apiKey: String,
        @Query("country") country: String
    ): Response<NewsResponse> // Return Response<NewsResponse>
}


object RetrofitInstance {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // Log the body of the request and response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: NewsAPIService by lazy {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .client(okHttpClient)  // Set the custom OkHttpClient with the interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsAPIService::class.java)
    }
}