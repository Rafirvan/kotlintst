package com.rafirvan.movgg.data.models

@kotlinx.serialization.Serializable
data class Video(
    val iso_639_1: String = "",
    val iso_3166_1: String = "",
    val name: String = "",
    val key: String = "",
    val site: String = "",
    val size: Int = 0,
    val type: String = "",
    val official: Boolean = true,
    val published_at: String = "",
    val id: String = ""
)