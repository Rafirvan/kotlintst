package com.rafirvan.movgg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafirvan.movgg.data.models.Movie
import com.rafirvan.movgg.ui.components.MovieCard
import com.rafirvan.movgg.viewmodel.MoviesViewModel

@Composable
fun MoviesScreen(
    viewModel: MoviesViewModel,
    genreId: Int,
    genreName: String,
    onMovieClick: (Movie) -> Unit,
    onBackClick: () -> Unit
) {
    val movies by viewModel.movies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var currentPage by remember { mutableIntStateOf(1) }

    LaunchedEffect(genreId) {
        if (genreId > 0) {
            viewModel.loadMoviesByGenre(genreId, currentPage)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(color = Color(0xFF800080)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                color = Color.White,
                text = "$genreName Movies",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading && movies.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(movies) { movie ->
                        MovieCard(
                            movie = movie,
                            onClick = { onMovieClick(movie) }
                        )
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator()
                            } else {
                                LaunchedEffect(Unit) {
                                    currentPage++
                                    viewModel.loadMoviesByGenre(genreId, currentPage)
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}
