package com.example.newsapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ArticleList(articles: List<Article>, onArticleClick: (Article) -> Unit) {
    LazyColumn {
        items(articles) { article ->
            ArticleItem(article = article, onClick = { onArticleClick(article) })
        }
    }
}
@Composable
fun ArticleItem(article: Article, onClick: () -> Unit) {
    val backgroundColor = if (article.content != null) {
        Color(0xFF8B0000) // Dark red for on-app
    } else {
        Color(0xFFFFCDD2) // Light red for off-app
    }
    val textColor = if (article.content != null) {
        Color(0xFFFFFFFF) // Dark red for on-app
    } else {
        Color(0xFF000000) // Light red for off-app
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            article.urlToImage?.let { imageUrl ->
                coil.compose.AsyncImage(
                    model = imageUrl,
                    contentDescription = "Article Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(bottom = 16.dp)
                )
            }
            Text(
                text = article.title ?: "No Title",
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = article.description ?: "No Description",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
    }
}



@Composable
fun ArticleCard(article: Article) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            article.urlToImage?.let {
                AsyncImage(
                    model = it,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Text(article.title, style = MaterialTheme.typography.titleMedium)
            article.description?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
