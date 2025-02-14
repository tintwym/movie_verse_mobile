package dev.team08.movieverse.data.api

import dev.team08.movieverse.domain.model.Review
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewApiService {
    @POST("api/reviews/{movieId}")
    suspend fun submitReview(
        @Path("movieId") movieId: Int,
        @Query("reviewText") reviewText: String,
        @Header("Authorization") token: String
    ): Response<String>

    @GET("api/reviews/{movieId}")
    suspend fun getReviews(
        @Path("movieId") movieId: Int,
        @Header("Authorization") token: String
    ): Response<List<Review>>

    @GET("api/reviews/{movieId}/user")
    suspend fun getReviewForMovie(
        @Header("Authorization") token: String,
        @Path("movieId") movieId: Int
    ): Review
}
