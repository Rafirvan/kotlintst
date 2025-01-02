package com.rafirvan.movgg.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafirvan.movgg.data.models.Genre
import com.rafirvan.movgg.data.network.TmdbService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GenresViewModel  (private val tmdbService: TmdbService) : ViewModel() {
    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _genres.value = tmdbService.getGenres()
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
}