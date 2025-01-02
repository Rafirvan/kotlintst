package com.rafirvan.movgg.data.models

@kotlinx.serialization.Serializable
data class Movie(
    val id: Int = 0,
    val title: String = "",
    val adult: Boolean = true,
    val genre_ids: List<Int> = emptyList(),
    val original_language: String = "",
    val original_title: String = "",
    val overview: String = "",
    val popularity: Double = 0.0,
    val poster_path: String = "",
    val release_date: String = "",
    val video: Boolean = true,
    val vote_average: Double = 0.0,
    val vote_count: Int = 0
)