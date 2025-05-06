package com.example.newsapp

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

val warmFilter = ColorMatrix().apply {
    setToScale(
        redScale = 1.1f,
        greenScale = 1.0f,
        blueScale = 0.8f,
        alphaScale = 1.0f
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    article: Article,
    onBack: () -> Unit,
    onSaveArticle: (Article) -> Unit,
    onEnableReaderMode: () -> Unit,
    isReaderMode: Boolean,
    isDarkMode: Boolean,
    articleDao: ArticleDao
) {
    var isTtsInitialized by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isSaved by remember { mutableStateOf(false) }
    var showWebView by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(article.title) {
        val count = articleDao.countByTitle(article.title ?: "")
        isSaved = count > 0
    }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            isTtsInitialized = status == TextToSpeech.SUCCESS
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    val backgroundColor = if (isDarkMode) {
        MaterialTheme.colorScheme.background
    } else {
        if (isReaderMode) Color(0xFFF4ECD8) else MaterialTheme.colorScheme.background
    }

    val textColor = if (isReaderMode) {
        if (isDarkMode) Color(0xFFF4ECD8) else Color(0xFF2C2C2C)
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    if (showWebView) {
        WebViewScreen(url = article.url ?: "", onClose = { showWebView = false })
    } else {
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = {
                            tts?.stop()
                            isSpeaking = false
                            onBack()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = onEnableReaderMode) {
                            Icon(painter = painterResource(id = R.drawable.visibility_24), contentDescription = "Toggle Reader Mode")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor,
                        navigationIconContentColor = textColor,
                        actionIconContentColor = textColor
                    )
                )
            },
            floatingActionButton = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Save FAB
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                onSaveArticle(article)
                                isSaved = true
                            },
                            containerColor = Color(0xFFA1474F),
                            modifier = Modifier.size(56.dp)
                        ) {
                            val savedPainter = painterResource(id = R.drawable.bookmark_check_24px)
                            val wantToSavePainter = painterResource(id = R.drawable.bookmark_24)

                            Icon(
                                painter = if (isSaved) savedPainter else wantToSavePainter,
                                contentDescription = "Save"
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSaved) "Saved" else "Save",
                            fontSize = 11.sp,
                            modifier = Modifier.width(56.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Listen FAB
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
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
                            containerColor = Color(0xFFA1474F),
                            modifier = Modifier.size(56.dp)
                        ) {
                            val painterSpeak = painterResource(id = R.drawable.volume_up_24px)
                            val painterPause = painterResource(id = R.drawable.pause_24px)
                            Icon(
                                painter = if (isSpeaking)painterPause  else painterSpeak,
                                contentDescription = if (isSpeaking) "Stop" else "Listen"
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSpeaking) "Stop" else "Listen",
                            fontSize = 11.sp,
                            modifier = Modifier.width(56.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // WebView FAB
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        FloatingActionButton(
                            onClick = { showWebView = true },
                            containerColor = Color(0xFFA1474F),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(painter = painterResource(id = R.drawable.language_24px), contentDescription = "Open Web")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Website", fontSize = 11.sp, modifier = Modifier.width(56.dp), textAlign = TextAlign.Center)
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
                            .padding(bottom = 16.dp),
                        colorFilter = if (isReaderMode) ColorFilter.colorMatrix(warmFilter) else null
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
}
