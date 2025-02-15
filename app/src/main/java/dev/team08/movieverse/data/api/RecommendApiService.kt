package dev.team08.movieverse.data.api

import retrofit2.http.GET

interface RecommendApiService {
    @GET("api/user-interactions/recommend")
    suspend fun getRecommendedMovieIds(): List<Int>
}
