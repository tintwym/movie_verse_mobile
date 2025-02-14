package dev.team08.movieverse.data.api

import com.google.gson.GsonBuilder
import dev.team08.movieverse.domain.model.Review
import dev.team08.movieverse.domain.model.ReviewResponse
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ReviewApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val reviewApiService: ReviewApiService = retrofit.create(ReviewApiService::class.java)

    suspend fun submitReview(movieId: Int, reviewText: String, token: String): Result<String> {
        return try {
            val response = reviewApiService.submitReview(
                movieId = movieId,
                reviewText = reviewText,
                token = "Bearer $token"
            )
            if (response.isSuccessful) {
                Result.success(response.body() ?: "Review submitted successfully")
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getReviews(movieId: Int, token: String): Result<List<Review>> {
        return try {
            val response = reviewApiService.getReviews(movieId, "Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviewForMovie(movieId: Int, token: String): Result<Review> {
        return try {
            val response = reviewApiService.getReviewForMovie(
                "Bearer $token",
                movieId
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
