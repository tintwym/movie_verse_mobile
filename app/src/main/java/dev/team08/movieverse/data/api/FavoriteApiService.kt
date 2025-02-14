package dev.team08.movieverse.data.api

import dev.team08.movieverse.domain.model.MovieInteraction
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApiService {
    @POST("api/user-interactions/favorite/{movieId}")
    suspend fun toggleFavorite(
        @Path("movieId") movieId: Int,
        @Header("Authorization") token: String
    ): Response<String>

    @GET("api/user-interactions/{movieId}")
    suspend fun getMovieInteraction(
        @Path("movieId") movieId: Int,
        @Header("Authorization") token: String
    ): Response<MovieInteraction>
}
