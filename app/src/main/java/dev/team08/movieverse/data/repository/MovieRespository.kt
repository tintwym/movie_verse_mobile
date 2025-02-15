package dev.team08.movieverse.data.repository

import android.util.Log
import dev.team08.movieverse.data.api.MovieApiClient
import dev.team08.movieverse.domain.model.Movie
import dev.team08.movieverse.domain.model.Genre
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class MovieRepository {
    private val movieApiService = MovieApiClient.apiService
    private var genresMap: Map<Int, String> = emptyMap()

    suspend fun getPopularMovies(apiKey: String): List<Movie> {
        Log.d("MovieRepository", "Fetching popular movies")
        return try {
            if (genresMap.isEmpty()) {
                val genreResponse = movieApiService.getGenres(apiKey)
                genresMap = genreResponse.genres.associate { it.id to it.name }
            }

            val response = movieApiService.getPopularMovies(apiKey)
            processMovieResults(response.results, apiKey)
        } catch (e: Exception) {
            Log.e("MovieRepository", "Error fetching popular movies", e)
            throw e
        }
    }

    // Changed from private to public
    suspend fun getMoviesByIds(movieIds: List<Int>, apiKey: String): List<Movie> {
        Log.d("MovieRepository", "Fetching movies by IDs: $movieIds")
        return try {
            if (genresMap.isEmpty()) {
                val genreResponse = movieApiService.getGenres(apiKey)
                genresMap = genreResponse.genres.associate { it.id to it.name }
            }

            coroutineScope {
                movieIds.map { movieId ->
                    async {
                        try {
                            val movieDetails = movieApiService.getMovieDetails(movieId, apiKey)
                            Log.d("MovieRepository", "Got details for movie $movieId")

                            val genres = movieDetails.genres?.map { genre ->
                                Genre(genre.id, genresMap[genre.id] ?: genre.name)
                            } ?: emptyList()

                            Movie(
                                id = movieId,
                                title = movieDetails.title ?: "",
                                overview = movieDetails.overview,
                                posterPath = movieDetails.posterPath,
                                backdropPath = movieDetails.backdropPath,
                                releaseDate = movieDetails.releaseDate,
                                voteAverage = movieDetails.voteAverage,
                                genreIds = genres.map { it.id },
                                runtime = movieDetails.runtime,
                                genres = genres
                            )
                        } catch (e: Exception) {
                            Log.e("MovieRepository", "Error fetching details for movie $movieId", e)
                            null
                        }
                    }
                }.mapNotNull { it.await() }
            }
        } catch (e: Exception) {
            Log.e("MovieRepository", "Error fetching movies by IDs", e)
            emptyList()
        }
    }

    private suspend fun processMovieResults(movies: List<Movie>, apiKey: String): List<Movie> {
        return coroutineScope {
            movies.map { movie ->
                async {
                    try {
                        val movieDetails = movieApiService.getMovieDetails(movie.id, apiKey)
                        Log.d("MovieRepository", "Processing movie ${movie.id}")

                        val genres = movie.genreIds?.mapNotNull { genreId ->
                            genresMap[genreId]?.let { genreName ->
                                Genre(genreId, genreName)
                            }
                        } ?: emptyList()

                        movie.copy(
                            runtime = movieDetails.runtime,
                            genres = genres
                        )
                    } catch (e: Exception) {
                        Log.e("MovieRepository", "Error processing movie ${movie.id}", e)
                        movie
                    }
                }
            }.map { it.await() }
        }
    }
}
