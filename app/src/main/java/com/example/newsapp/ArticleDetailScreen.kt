package com.example.newsapp

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    article: Article,
    onBack: () -> Unit,
    onSaveArticle: (Article) -> Unit,
    onEnableReaderMode: () -> Unit,
    onReadAloud: (String) -> Unit,
    isReaderMode: Boolean
) {
    var isTtsInitialized by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            isTtsInitialized = status == TextToSpeech.SUCCESS
        }
    }

    // Stop TTS when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    val backgroundColor = if (isReaderMode) Color(0xFFF4ECD8) else MaterialTheme.colorScheme.background
    val textColor = if (isReaderMode) Color(0xFF2C2C2C) else MaterialTheme.colorScheme.onBackground

    Scaffold(
        containerColor = backgroundColor,
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
                    IconButton(onClick = {
                        tts?.stop()
                        isSpeaking = false
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Show Save and Reader Mode buttons only if not speaking
                if (!isSpeaking && article.content != null) {
                    FloatingActionButton(
                        onClick = { onSaveArticle(article) },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Save Article")
                    }

                    FloatingActionButton(
                        onClick = onEnableReaderMode,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Reader Mode")
                    }
                }

                // Read Aloud Button (center)
                FloatingActionButton(
                    onClick = {
                        if (isTtsInitialized) {
                            if (tts?.isSpeaking == true) {
                                tts?.stop()
                                isSpeaking = false
                            } else {
                                tts?.speak(
                                    article.content ?: "No content available.",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    null
                                )
                                isSpeaking = true
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.ArrowBack else Icons.Default.Call,
                        contentDescription = if (isSpeaking) "Stop" else "Read Aloud"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(backgroundColor)
        ) {
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

            Text(
                text = article.title ?: "No Title Available",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp),
                color = textColor
            )

            Text(
                text = article.content ?: "No content available.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp),
                color = textColor
            )
        }
    }
}
