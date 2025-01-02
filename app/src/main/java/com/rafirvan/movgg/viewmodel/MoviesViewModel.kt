package com.rafirvan.movgg.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafirvan.movgg.data.models.Movie
import com.rafirvan.movgg.data.network.TmdbService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoviesViewModel(private val tmdbService: TmdbService) : ViewModel() {
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadMoviesByGenre(genreId: Int, page: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newMovies = tmdbService.getMoviesByGenre(genreId, page)
                _movies.value += newMovies
            } finally {
                _isLoading.value = false
            }
        }
    }
}
