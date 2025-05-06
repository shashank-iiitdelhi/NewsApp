package com.example.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.newsapp.ui.theme.NewsAppTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import android.content.Context
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(onSettingsClicked: () -> Unit) {
    TopAppBar(
        title = { Text("Real News App") },
        actions = {
            IconButton(onClick = onSettingsClicked) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    )
}
class MainActivity : ComponentActivity() {
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Initialize Room database *inside* the composable scope
            val context = LocalContext.current
            val db = remember { AppDatabase.getDatabase(context) }
            val articleDao = db.articleDao()
            var showSettingsScreen by remember { mutableStateOf(false) }
            var selectedArticle by remember { mutableStateOf<Article?>(null) }
            var showWebView by remember { mutableStateOf(false) }
            var selectedScreen by remember { mutableStateOf("Top") }
            var isReaderMode by remember { mutableStateOf(false) }  // Track Reader Mode
            val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            var darkTheme by remember { mutableStateOf(sharedPreferences.getBoolean("dark_mode", false)) }

            NewsAppTheme(darkTheme = darkTheme) {
                LaunchedEffect(selectedScreen) {
                    if (selectedScreen == "Trending") {
                        viewModel.fetchNews()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AppTopBar(onSettingsClicked = { showSettingsScreen = true })
                    },
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
                        if (showSettingsScreen) {
                            SettingsScreen(
                                isDarkMode = darkTheme,
                                onDarkModeToggle = { enabled ->
                                    darkTheme = enabled
                                    with(sharedPreferences.edit()) {
                                        putBoolean("dark_mode", enabled)
                                        apply()
                                    }
                                },
                                onDismiss = { showSettingsScreen = false }
                            )
                        }
                        else {
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
                                                val titleToCheck = entity.title ?: ""
                                                // Check if an article with the same title already exists
                                                val existingArticleCount = articleDao.countByTitle(titleToCheck)

                                                // If the count is 0, the article does not exist, so insert it into the database
                                                if (existingArticleCount == 0) {
                                                    articleDao.insertArticle(entity)
                                                } else {
                                                    // Optionally, log that the article already exists or perform other actions
                                                    Log.d("ArticleSave", "Article with title '${entity.title}' already exists.")
                                                }
                                            }
                                        },

                                        onEnableReaderMode = {
                                            isReaderMode = !isReaderMode // Toggle Reader Mode
                                        },
                                        isReaderMode = isReaderMode,  // Pass reader mode state
                                        isDarkMode = darkTheme,
                                        articleDao = articleDao
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
//                                Text("Current country: ${viewModel.selectedCountry}")
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
                                            description = savedArticle.description ?: "",
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
}
