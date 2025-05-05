package com.example.newsapp

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    article: Article,
    onBack: () -> Unit,
    onSaveArticle: (Article) -> Unit, // To save article locally
    onReadAloud: (String) -> Unit, // Text to speech
    onEnableReaderMode: () -> Unit // Enable reader mode
) {
    var isTtsInitialized by remember { mutableStateOf(false) }
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    // Initialize TextToSpeech
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            isTtsInitialized = status == TextToSpeech.SUCCESS
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Real News")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            // Floating Action Buttons positioned together
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Save Article Button
                if (article.content != null) {
                    FloatingActionButton(
                        onClick = { onSaveArticle(article) },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Save Article")
                    }
                }

                // Read Aloud Button
                FloatingActionButton(
                    onClick = {
                        if (isTtsInitialized) {
                            tts?.speak(
                                article.content ?: "No content available.",
                                TextToSpeech.QUEUE_FLUSH, null, null
                            )
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Read Aloud")
                }

                // Reader Mode Button
                FloatingActionButton(
                    onClick = { onEnableReaderMode() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Reader Mode")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Enable scrolling
        ) {
            // Image section
            article.urlToImage?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Article Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(bottom = 16.dp)
                )
            }

            // Article title
            Text(
                text = article.title ?: "No Title Available",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Article content
            Text(
                text = article.content ?: "No content available.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}