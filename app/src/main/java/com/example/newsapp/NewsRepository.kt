package com.example.newsapp
import android.util.Log
import retrofit2.Response

class NewsRepository {

    // Function to fetch top headlines based on country and API key
    suspend fun getNews(apiKey: String, country: String): List<Article> {
        return fetchNews(apiKey = apiKey, country = country)
    }


    // Function to fetch trending news by category
    suspend fun getTrendingNewsByCategory(apiKey: String, country: String, category: String): List<Article> {
        return fetchTrendingNewsByCategory(apiKey, country, category)
    }

    // Private function to fetch news articles (common logic for top headlines and trending news)
    private suspend fun fetchNews(apiKey: String, country: String): List<Article> {
        return try {
            // Making the API call and getting the Response<NewsResponse>
            val response = RetrofitInstance.api.getTopHeadlines(
                country = country,
                apiKey = apiKey
            )

            // Check if the response is successful
            if (response.isSuccessful) {
                // Get the articles from the response body
                val articles = response.body()?.articles ?: emptyList()
                Log.d("NewsRepository", "Fetched ${articles.size} articles for country: $country")
                articles
            } else {
                // Log error if the response is not successful
                Log.e("NewsRepository", "Error: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            // Log the exception if an error occurs during the API call
            Log.e("NewsRepository", "Error fetching news", e)
            emptyList()
        }
    }

    // Private function to fetch trending news by category
    private suspend fun fetchTrendingNewsByCategory(apiKey: String, country: String, category: String): List<Article> {
        return try {
            // Making the API call and getting the Response<NewsResponse>
            val response: Response<NewsResponse> = RetrofitInstance.api.getTrendingNewsByCategory(apiKey, category, country)

            // Check if the response is successful
            if (response.isSuccessful) {
                // Get the articles from the response body
                val articles = response.body()?.articles ?: emptyList()
                Log.d("NewsRepository", "Fetched ${articles.size} trending articles for category: $category in country: $country")
                articles
            } else {
                // Log error if the response is not successful
                Log.e("NewsRepository", "Error: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            // Log the exception if an error occurs during the API call
            Log.e("NewsRepository", "Error fetching trending news", e)
            emptyList()
        }
    }
}
