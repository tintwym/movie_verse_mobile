package dev.team08.movieverse.data.repository

import android.util.Log
import dev.team08.movieverse.data.api.MovieApiClient
import dev.team08.movieverse.domain.model.Movie
import dev.team08.movieverse.domain.model.Genre

class MovieRepository {
    private val apiService = MovieApiClient.apiService
    private var genresMap: Map<Int, String> = emptyMap()

    suspend fun getPopularMovies(apiKey: String): List<Movie> {
        Log.d("MovieRepository", "Using API Key: $apiKey")
        return try {
            // Get genres if we haven't already
            if (genresMap.isEmpty()) {
                val genreResponse = apiService.getGenres(apiKey)
                genresMap = genreResponse.genres.associate { it.id to it.name }
            }

            // Get popular movies
            val response = apiService.getPopularMovies(apiKey)

            // Process each movie to include genres and runtime
            response.results.map { movie ->
                val movieDetails = try {
                    apiService.getMovieDetails(movie.id, apiKey)
                } catch (e: Exception) {
                    Log.e("MovieRepository", "Error fetching details for movie ${movie.id}", e)
                    null
                }

                // Map genre IDs to Genre objects
                val genres = movie.genreIds?.mapNotNull { genreId ->
                    genresMap[genreId]?.let { genreName ->
                        Genre(genreId, genreName)
                    }
                } ?: emptyList()

                // Create movie with all details
                movie.copy(
                    runtime = movieDetails?.runtime,
                    genres = genres
                )
            }
        } catch (e: Exception) {
            Log.e("MovieRepository", "Error fetching popular movies", e)
            throw e
        }
    }
}
