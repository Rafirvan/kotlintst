package com.rafirvan.movgg.data.network

import android.util.Log
import com.rafirvan.movgg.data.models.Genre
import com.rafirvan.movgg.data.models.GenreResponse
import com.rafirvan.movgg.data.models.Movie
import com.rafirvan.movgg.data.models.MovieResponse
import com.rafirvan.movgg.data.models.Video
import com.rafirvan.movgg.data.models.VideoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*

class TmdbService(
    private val apiKey: String
) {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    fun cleanup() {
        httpClient.close()
    }

    suspend fun getGenres(language: String = "en-US"): List<Genre> {
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = httpClient.get("https://api.themoviedb.org/3/genre/movie/list") {
                    header("Authorization", "Bearer $apiKey")
                    parameter("language", language)
                }
                Log.d("genres",response.body())

                if (response.status.value != 200) {
                    throw Exception("Error fetching genres, status: ${response.status.value}")
                }

                return@withContext response.body<GenreResponse>().genres
            } catch (e: Exception) {
                throw Exception("Failed to fetch genres: ${e.message}")
            }
        }
    }



    suspend fun getMoviesByGenre(genreId: Int, page: Int = 1): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = httpClient.get("https://api.themoviedb.org/3/discover/movie") {
                    header("Authorization", "Bearer $apiKey")
                    parameter("with_genres", genreId.toString())
                    parameter("page", page.toString())
                }
                Log.d("genres",response.body())


                if (response.status.value != 200) {
                    throw Exception("Error fetching movies, status: ${response.status.value}")
                }

                return@withContext response.body<MovieResponse>().results
            } catch (e: Exception) {
                throw Exception("Failed to fetch movies: ${e.message}")
            }
        }
    }

    suspend fun getMovieDetails(movieId: Int): Movie {
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = httpClient.get("https://api.themoviedb.org/3/movie/$movieId") {
                    header("Authorization", "Bearer $apiKey")
                }

                if (response.status.value != 200) {
                    throw Exception("Error fetching movie details, status: ${response.status.value}")
                }

                return@withContext response.body<Movie>()
            } catch (e: Exception) {
                throw Exception("Failed to fetch movie details: ${e.message}")
            }
        }
    }

    suspend fun getMovieVideos(movieId: Int): List<Video> {
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = httpClient.get("https://api.themoviedb.org/3/movie/$movieId/videos") {
                    header("Authorization", "Bearer $apiKey")
                }
                Log.d("getVid", movieId.toString())


                if (response.status.value != 200) {
                    throw Exception("Error fetching videos, status: ${response.status.value}")
                }

                return@withContext response.body<VideoResponse>().results
            } catch (e: Exception) {
                throw Exception("Failed to fetch videos: ${e.message}")
            }
        }
    }
}
