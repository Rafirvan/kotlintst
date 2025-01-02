package com.rafirvan.movgg.data.models

@kotlinx.serialization.Serializable
data class GenreResponse(
    val genres: List<Genre>
)

@kotlinx.serialization.Serializable
data class MovieResponse(
    val results: List<Movie>
)

@kotlinx.serialization.Serializable
data class VideoResponse(
    val id: Int = 0,
    val results: List<Video> = emptyList()
)