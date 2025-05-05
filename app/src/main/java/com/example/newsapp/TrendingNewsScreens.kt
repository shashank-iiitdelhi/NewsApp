package com.example.newsapp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.size.Scale
import coil.compose.AsyncImage

@Composable
fun TrendingNewsScreen(viewModel: NewsViewModel, onArticleClick: (Article) -> Unit) {
    val trendingTopics = viewModel.trendingTopics
    val articles = viewModel.articles
    val expandedTopic = remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Trending Topics", style = MaterialTheme.typography.headlineMedium)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(trendingTopics) { topic ->
                val article = articles.find { it.title?.contains(topic, ignoreCase = true) == true }
                TrendingTopicItem(
                    topic = topic,
                    isExpanded = expandedTopic.value == topic,
                    article = article,
                    onTopicClick = {

                        expandedTopic.value = if (expandedTopic.value == topic) null else topic
                    },
                    onArticleClick = {
                        article?.let { onArticleClick(it) }
                    }
                )
            }
        }
    }
}


@Composable
fun TrendingTopicItem(
    topic: String,
    isExpanded: Boolean,
    article: Article?,
    onTopicClick: () -> Unit,
    onArticleClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onTopicClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = topic, style = MaterialTheme.typography.headlineSmall)

            if (isExpanded && article != null) {
                article.urlToImage?.let { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Article Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(top = 8.dp)
                    )
                }

                Text(
                    text = article.title ?: "No title",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { onArticleClick() }
                )
            }
        }
    }
}
