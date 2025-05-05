package com.example.newsapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.nio.file.WatchEvent
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun SavedArticlesScreen(
    articleDao: ArticleDao,
    onArticleSelected: (ArticleEntity) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var savedArticles by remember { mutableStateOf<List<ArticleEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            savedArticles = articleDao.getAllSavedArticles()
        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Show the appropriate message when there are no saved articles
        if (savedArticles.isEmpty()) {
            Text("You have not saved any articles", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Display saved articles
            Text("Your saved articles", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterHorizontally))

            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(savedArticles, key = { it.id }) { article ->
                    val backgroundColor = if (article.isRead) {
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) // Light red
                    } else {
                        MaterialTheme.colorScheme.error // Darker red
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                coroutineScope.launch {
                                    articleDao.markArticleAsRead(article.id)
                                    savedArticles = articleDao.getAllSavedArticles() // Refresh
                                }
                                onArticleSelected(article)
                            },
                        colors = CardDefaults.cardColors(containerColor = backgroundColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                article.title ?: "No Title",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                article.content ?: "No content",
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}