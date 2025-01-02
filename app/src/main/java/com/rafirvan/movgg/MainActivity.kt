package com.rafirvan.movgg

import GenresScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rafirvan.movgg.data.network.TmdbService
import com.rafirvan.movgg.ui.screens.MoviesScreen
import com.rafirvan.movgg.ui.screens.SelectedMovieScreen
import com.rafirvan.movgg.viewmodel.GenresViewModel
import com.rafirvan.movgg.viewmodel.MoviesViewModel
import com.rafirvan.movgg.viewmodel.SelectedMovieViewModel

public class MainActivity : ComponentActivity() {
    val tmdbService = TmdbService(BuildConfig.TMDB_API_KEY)
    private val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(GenresViewModel::class.java) ->
                    GenresViewModel(tmdbService) as T
                modelClass.isAssignableFrom(MoviesViewModel::class.java) ->
                    MoviesViewModel(tmdbService) as T
                modelClass.isAssignableFrom(SelectedMovieViewModel::class.java) ->
                    SelectedMovieViewModel(tmdbService) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tmdbService.cleanup()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                BackHandler {
                    if (navController.currentBackStackEntry?.destination?.route != "genres") {
                        navController.navigateUp()
                    } else {
                        finish()
                    }
                }

                NavHost(navController = navController, startDestination = "genres") {
                    composable("genres") {
                        val viewModel: GenresViewModel = viewModel(factory = viewModelFactory)
                        GenresScreen(
                            viewModel = viewModel,
                            onGenreClick = { genre ->
                                try {
                                    if (genre.id != null && genre.id > 0) {
                                        navController.navigate("movies/${genre.id}/${genre.name}") {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Navigation error: ${e.message}")
                                }
                            }
                        )
                    }

                    composable(
                        "movies/{genreId}/{genreName}",
                        arguments = listOf(navArgument("genreId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val genreId = backStackEntry.arguments?.getInt("genreId") ?: return@composable
                        val genreName = backStackEntry.arguments?.getString("genreName") ?: "Unknown Genre"
                        val viewModel: MoviesViewModel = viewModel(factory = viewModelFactory)
                        MoviesScreen(
                            viewModel = viewModel,
                            genreId = genreId,
                            genreName = genreName,
                            onBackClick = { navController.navigateUp() },
                            onMovieClick = { movie ->
                                navController.navigate("movie/${movie.id}")
                            }
                        )
                    }

                    composable(
                        "movie/{movieId}",
                        arguments = listOf(navArgument("movieId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
                        val viewModel: SelectedMovieViewModel = viewModel(factory = viewModelFactory)
                        SelectedMovieScreen(
                            viewModel = viewModel,
                            movieId = movieId,
                            onBackClick = { navController.navigateUp() }
                        )
                    }
                }
            }
        }
    }
}
