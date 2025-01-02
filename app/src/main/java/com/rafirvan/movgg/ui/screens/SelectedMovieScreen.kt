package com.rafirvan.movgg.ui.screens

import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.rafirvan.movgg.viewmodel.SelectedMovieViewModel

private class WebViewState {
    private var webView: WebView? = null

    fun loadContent(videoKey: String) {
        webView?.loadData(
            """
            <html>
                <head>
                    <style>
                        body, html {
                            margin: 0;
                            padding: 0;
                            width: 100%;
                            height: 100%;
                        }
                        .video-container {
                            position: relative;
                            width: 100%;
                            height: 0;
                            padding-bottom: 56.25%;
                        }
                        .video-container iframe {
                            position: absolute;
                            top: 0;
                            left: 0;
                            width: 100%;
                            height: 100%;
                            border: 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="video-container">
                        <iframe
                            src="https://www.youtube.com/embed/${videoKey}"
                            frameborder="0"
                            allow="autoplay; fullscreen"
                            allowfullscreen>
                        </iframe>
                    </div>
                </body>
            </html>
            """.trimIndent(),
            "text/html",
            "UTF-8"
        )
    }

    fun attachWebView(view: WebView) {
        webView = view
    }
}

@Composable
private fun rememberWebViewState(): WebViewState {
    return remember { WebViewState() }
}

@Composable
fun SelectedMovieScreen(
    viewModel: SelectedMovieViewModel,
    movieId: Int,
    onBackClick: () -> Unit
) {
    val movie by viewModel.movie.collectAsState()
    val video by viewModel.video.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val webViewState = rememberWebViewState()

    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
    }

    // Preload WebView when video key is available
    LaunchedEffect(video?.key) {
        video?.key?.let { key ->
            webViewState.loadContent(key)
        }
    }

    if (isLoading && movie == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        movie?.let { movieData ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box {
                    // Poster Image or Fallback
                    if (movieData.poster_path != null) {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w500${movieData.poster_path}",
                            contentDescription = movieData.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(600.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(600.dp)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No Image Available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Title and Score Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(600.dp)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        androidx.compose.ui.graphics.Color.Transparent,
                                        androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f)
                                    ),
                                    startY = 300f
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = movieData.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = androidx.compose.ui.graphics.Color.White
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = androidx.compose.ui.graphics.Color.Yellow
                            )
                            Text(
                                text = " ${movieData.vote_average}/10",
                                style = MaterialTheme.typography.bodyLarge,
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }
                }


                Text(
                    text = "Release date: ${movieData.release_date}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )

                Text(
                    text = movieData.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )

                val vidKey = video?.key
                if (vidKey != null) {
                    var isWebViewLoaded by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f)
                            .padding(16.dp)
                    ) {
                        if (!isWebViewLoaded) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        AndroidView(
                            factory = { context ->
                                WebView(context).apply {
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    settings.cacheMode = WebSettings.LOAD_NO_CACHE
                                    webViewClient = object : WebViewClient() {
                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            isWebViewLoaded = true
                                        }
                                    }
                                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                    webViewState.attachWebView(this)
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(if (isWebViewLoaded) 1f else 0f)
                        )
                    }
                }
            }
        }
    }
}