package com.example.newsapp

import android.util.Log
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val repository = NewsRepository()

    // Holds the list of articles fetched from the API
    var articles by mutableStateOf<List<Article>>(emptyList())
        private set

    // Holds the trending topics/keywords
    var trendingTopics by mutableStateOf<List<String>>(emptyList())
        private set

    var selectedCountry by mutableStateOf("us")
        private set

    init {
        fetchNews()
    }

    // Function to select a country and fetch news for that country
    fun onCountrySelected(country: String) {
        selectedCountry = country
        fetchNews()
    }

    // Fetch top news for a given country
    fun fetchNews() {
        viewModelScope.launch {
            try {
                val articlesRetrieved = repository.getNews("8fbc76d60fab4c0bba42941d323fac09", selectedCountry)
//                val articlesRetrieved = repository.getNews("8", selectedCountry)
                Log.d("NewsViewModel", "Fetched ${articlesRetrieved.size} articles")
                articles = articlesRetrieved

                // Extract topics/keywords from the fetched articles (e.g., article titles)
                trendingTopics = extractUniqueTrendingTopics(articlesRetrieved)
                    .map { it.lowercase().replaceFirstChar { c -> c.uppercase() } }
                    .distinct()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("NewsViewModel", "Error fetching news", e)
            }
        }
    }


    private fun extractUniqueTrendingTopics(articles: List<Article>): List<String> {
        val wordFrequency = mutableMapOf<String, Int>()
//        val stopwords = setOf(
//            "a", "an", "the", "and", "or", "but", "if", "then", "else", "for", "on", "in", "at", "by", "with", "about", "against",
//            "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "out", "over",
//            "under", "again", "further", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few",
//            "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very",
//            "can", "will", "just", "don", "should", "now", "this", "that", "these", "those", "news",
//
//            // Modal Verbs (to be filtered out)
//            "could", "should", "would",
//
//            // Fractions
//            "half", "quarter", "third", "fifth", "tenth", "twenty", "percent",
//
//            // Pronouns
//            "i", "me", "my", "myself", "we", "our", "ours", "ourselves",
//            "you", "your", "yours", "yourself", "yourselves",
//            "he", "him", "his", "himself", "she", "her", "hers", "herself",
//            "it", "its", "itself", "they", "them", "their", "theirs", "themselves",
//
//            // Verbs (common auxiliaries and linking)
//            "is", "am", "are", "was", "were", "be", "been", "being",
//            "have", "has", "had", "having",
//            "do", "does", "did", "doing",
//
//            // Other common filler words
//            "here", "there", "where", "when", "why", "how", "all", "any", "each", "which", "who", "whom", "that", "these", "those", "year","month","date"
//        )
        val stopwords = setOf(
            // Articles, Conjunctions, Prepositions
            "a", "an", "the", "and", "or", "but", "if", "then", "else", "for", "on", "in", "at", "by", "with", "about", "against",
            "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "out", "over",
            "under", "again", "further", "here", "there", "when", "where", "why", "how",

            // Quantifiers & Intensifiers
            "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same",
            "so", "than", "too", "very",

            // Modals
            "can", "will", "just", "don", "should", "could", "would",

            // Time & Reference
            "now", "this", "that", "these", "those", "year", "month", "day", "date", "time", "news",

            // Fractions and Percentages
            "half", "quarter", "third", "fifth", "tenth", "twenty", "percent",

            // Pronouns
            "i", "me", "my", "myself", "we", "our", "ours", "ourselves",
            "you", "your", "yours", "yourself", "yourselves",
            "he", "him", "his", "himself", "she", "her", "hers", "herself",
            "it", "its", "itself", "they", "them", "their", "theirs", "themselves",

            // Common auxiliary and linking verbs
            "is", "am", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "having",
            "do", "does", "did", "doing",

            // Popular base verbs (general-purpose)
            "go", "get", "make", "take", "come", "see", "know", "think", "say", "use", "want", "like", "need", "feel", "look", "give", "find",
            "work", "call", "try", "ask", "put", "mean", "keep", "let", "help", "show", "talk", "turn", "start", "begin", "leave", "live",

            // Basic common nouns
            "thing", "man", "woman", "person", "people", "child", "way", "world", "life", "case", "place", "week", "part", "group", "company",
            "system", "problem", "fact", "example", "state", "area", "number", "story", "school", "government", "country", "home"
        )

        // Step 1 & 2: Collect words from titles/descriptions
        articles.forEach { article ->
            val content = "${article.title} ${article.description}".lowercase()
            val words = content.split("\\W+".toRegex())
            for (word in words) {
                if (word.length > 3 && word !in stopwords) {
                    wordFrequency[word] = wordFrequency.getOrDefault(word, 0) + 1
                }
            }
        }

        // Step 3 & 4: Sort words by frequency
        val sortedWords = wordFrequency.entries
            .sortedByDescending { it.value }
            .map { it.key }

        // Step 5: Filter overlapping topics
        val uniqueTopics = mutableListOf<String>()
        for (word in sortedWords) {
            if (uniqueTopics.none { existing -> word in existing || existing in word }) {
                uniqueTopics.add(word)
            }
            if (uniqueTopics.size >= 10) break
        }

        return uniqueTopics
    }
}
