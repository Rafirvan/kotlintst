package com.rafirvan.movgg.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafirvan.movgg.data.models.Movie
import com.rafirvan.movgg.data.models.Video
import com.rafirvan.movgg.data.network.TmdbService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SelectedMovieViewModel(private val tmdbService: TmdbService) : ViewModel() {
    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie.asStateFlow()

    private val _video = MutableStateFlow<Video?>(null)
    val video: StateFlow<Video?> = _video.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadMovie(movieId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _movie.value = tmdbService.getMovieDetails(movieId)
                val videos = tmdbService.getMovieVideos(movieId)
                    _video.value = videos.firstOrNull { it.site.equals("YouTube", ignoreCase = true) }
            } finally {
                _isLoading.value = false
            }
        }
    }
}