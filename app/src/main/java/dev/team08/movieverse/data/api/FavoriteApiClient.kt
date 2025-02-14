package dev.team08.movieverse.data.api

import dev.team08.movieverse.domain.model.MovieInteraction
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class FavoriteApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val favoriteApiService: FavoriteApiService = retrofit.create(FavoriteApiService::class.java)

    suspend fun toggleFavorite(movieId: Int, token: String): Result<String> {
        return try {
            val response = favoriteApiService.toggleFavorite(movieId, "Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body() ?: "Favorite status updated.")
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMovieInteraction(movieId: Int, token: String): Result<MovieInteraction> {
        return try {
            val response = favoriteApiService.getMovieInteraction(movieId, "Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
