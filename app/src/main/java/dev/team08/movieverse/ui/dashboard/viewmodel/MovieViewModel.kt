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
    private val repository = MovieRepository()

    private val _popularMovies = MutableLiveData<List<Movie>>()
    val popularMovies: LiveData<List<Movie>> = _popularMovies

    private val _recommendedMovies = MutableLiveData<List<Movie>>()
    val recommendedMovies: LiveData<List<Movie>> = _recommendedMovies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _upcomingMovies = MutableLiveData<List<Movie>>()
    val upcomingMovies: LiveData<List<Movie>> = _upcomingMovies

    private var isLoggedIn = false
    private var currentJob: Job? = null

    init {
        loadPopularMovies()
    }

    fun setLoggedInStatus(status: Boolean) {
        if (isLoggedIn == status) return
        isLoggedIn = status

        loadPopularMovies()
        if (isLoggedIn) {
            loadRecommendedMovies()
        } else {
            _recommendedMovies.value = emptyList()
        }
    }

    private fun loadRecommendedMovies() {
        Log.d("MovieViewModel", "Loading recommended movies")
        viewModelScope.launch {
            try {
                val moviesList = repository.getMoviesByIds(RECOMMENDED_MOVIE_IDS, Constants.API_KEY)
                Log.d("MovieViewModel", "Loaded ${moviesList.size} recommended movies")
                if (moviesList.isNotEmpty()) {
                    _recommendedMovies.value = moviesList
                } else {
                    _error.value = "No recommended movies found"
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error loading recommended movies", e)
                _error.value = e.message
            }
        }
    }

    private fun loadPopularMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val moviesList = repository.getPopularMovies(Constants.API_KEY)
                _popularMovies.value = moviesList
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error loading popular movies", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUpcomingMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = MovieApiClient.apiService.getUpcomingMovies(Constants.API_KEY)
                _upcomingMovies.value = response.results
                Log.d("MovieViewModel", "Loaded ${response.results.size} upcoming movies")
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error loading upcoming movies", e)
                _error.value = e.message
                _upcomingMovies.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchMovies(query: String) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = MovieApiClient.apiService.searchMovies(query)
                if (response.results.isNotEmpty()) {
                    _popularMovies.value = response.results
                    _recommendedMovies.value = emptyList()
                } else {
                    _popularMovies.value = emptyList()
                    _recommendedMovies.value = emptyList()
                    _error.value = "No movies found for '$query'"
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error searching movies", e)
                _error.value = "Error searching movies: ${e.message}"
                _popularMovies.value = emptyList()
                _recommendedMovies.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reloadCurrentMovies() {
        loadPopularMovies()
        if (isLoggedIn) {
            loadRecommendedMovies()
        }
    }

    companion object {
        private val RECOMMENDED_MOVIE_IDS = listOf(
            916035, 464446, 320367, 50472, 1975, 11811, 46441, 7978,
            13788, 513268, 560362, 323368, 632357, 10900, 287084,
            11456, 45650, 13803, 10555, 291413
        )
    }
}
