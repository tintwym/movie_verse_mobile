package dev.team08.movieverse.ui.dashboard.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.team08.movieverse.common.Constants
import dev.team08.movieverse.data.api.MovieApiClient
import dev.team08.movieverse.data.repository.MovieRepository
import dev.team08.movieverse.domain.model.Movie
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _upcomingMovies = MutableLiveData<List<Movie>>()
    val upcomingMovies: LiveData<List<Movie>> = _upcomingMovies

    init {
        loadPopularMovies()
    }

    // Add this at the top of MovieViewModel with other LiveData declarations
    private val _recommendedMovies = MutableLiveData<List<Movie>>()
    val recommendedMovies: LiveData<List<Movie>> = _recommendedMovies

    // Add these movie IDs after your class variables
    private val recommendedMovieIds = listOf(
        550,  // Fight Club
        238,  // The Godfather
        424,  // Schindler's List
        680,  // Pulp Fiction
        155   // The Dark Knight
    )

    // Add this function to your MovieViewModel class
    fun loadRecommendedMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val recommendedMoviesList = mutableListOf<Movie>()

                for (movieId in recommendedMovieIds) {
                    try {
                        val movieDetail = MovieApiClient.apiService.getMovieDetails(
                            movieId = movieId,
                            apiKey = Constants.API_KEY
                        )
                        Log.d("MovieViewModel", "Movie detail loaded: ${movieDetail.title}, poster: ${movieDetail.posterPath}")

                        // Convert MovieDetailResponse to Movie
                        val movie = Movie(
                            id = movieDetail.id,
                            title = movieDetail.title,
                            overview = movieDetail.overview,
                            posterPath = movieDetail.posterPath,
                            backdropPath = movieDetail.backdropPath,
                            releaseDate = movieDetail.releaseDate,
                            voteAverage = movieDetail.voteAverage,
                            runtime = movieDetail.runtime,
                            genres = movieDetail.genres
                        )
                        recommendedMoviesList.add(movie)
                    } catch (e: Exception) {
                        Log.e("MovieViewModel", "Error loading movie details for ID: $movieId", e)
                    }
                }

                _recommendedMovies.value = recommendedMoviesList
                Log.d("MovieViewModel", "Recommended movies loaded: ${recommendedMoviesList.size}")
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error loading recommended movies", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPopularMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = MovieApiClient.apiService.getPopularMovies()
                _movies.value = response.results
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error loading movies", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = MovieApiClient.apiService.searchMovies(query)
                if (response.results.isNotEmpty()) {
                    _movies.value = response.results  // Update the same movies LiveData
                } else {
                    _movies.value = emptyList()
                    _error.value = "No movies found for '$query'"
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error searching movies", e)
                _error.value = "Error searching movies: ${e.message}"
                _movies.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUpcomingMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = MovieApiClient.apiService.getUpcomingMovies()
                _upcomingMovies.value = response.results
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error loading upcoming movies", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
