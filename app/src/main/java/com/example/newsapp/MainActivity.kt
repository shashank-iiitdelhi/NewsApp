package com.example.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.newsapp.WebViewScreen
import com.example.newsapp.ui.theme.NewsAppTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Initialize Room database *inside* the composable scope
            val context = LocalContext.current
            val db = remember { AppDatabase.getDatabase(context) }
            val articleDao = db.articleDao()

            var selectedArticle by remember { mutableStateOf<Article?>(null) }
            var showWebView by remember { mutableStateOf(false) }
            var selectedScreen by remember { mutableStateOf("Top") }

            NewsAppTheme {
                LaunchedEffect(selectedScreen) {
                    if (selectedScreen == "Trending") {
                        viewModel.fetchNews()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (selectedArticle == null && !showWebView) {
                            BottomNavigationBar(
                                selectedScreen = selectedScreen,
                                onTabSelected = {
                                    selectedScreen = it
                                    selectedArticle = null
                                    showWebView = false
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        when {
                            selectedArticle != null && !showWebView -> {
                                ArticleDetailScreen(
                                    article = selectedArticle!!,
                                    onBack = {
                                        selectedArticle = null
                                        showWebView = false
                                    },
                                    onSaveArticle = { article ->
                                        val entity = ArticleEntity(
                                            title = article.title,
                                            content = article.content,
                                            url = article.url,
                                            description = article.description,
                                            urlToImage = article.urlToImage
                                        )
                                        // Use coroutine scope for DB call
                                        CoroutineScope(Dispatchers.IO).launch {
                                            articleDao.insertArticle(entity)
                                        }
                                    },
                                    onReadAloud = { /* Optional */ },
                                    onEnableReaderMode = { showWebView = true }
                                )
                            }

                            selectedArticle != null && showWebView -> {
                                WebViewScreen(
                                    url = selectedArticle?.url ?: "",
                                    onClose = { showWebView = false }
                                )
                            }

                            selectedScreen == "Top" -> {
                                CountrySelector(
                                    selectedCountry = viewModel.selectedCountry,
                                    onCountrySelected = { viewModel.onCountrySelected(it) }
                                )
                                Text("Current country: ${viewModel.selectedCountry}")
                                ArticleList(viewModel.articles) { article ->
                                    selectedArticle = article
                                    showWebView = article.content.isNullOrBlank()
                                }
                            }

                            selectedScreen == "Trending" -> {
                                TrendingNewsScreen(viewModel) { article ->
                                    selectedArticle = article
                                    showWebView = article.content.isNullOrBlank()
                                }
                            }
                            selectedScreen == "Saved" -> {
                                SavedArticlesScreen(articleDao = articleDao) { savedArticle ->
                                    selectedArticle = Article(
                                        title = savedArticle.title ?: "",
                                        content = savedArticle.content,
                                        url = savedArticle.url ?: "",
                                        description = savedArticle.description?:"",
                                        urlToImage = savedArticle.urlToImage
                                    )
                                    showWebView = savedArticle.content.isNullOrBlank()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
