package dev.team08.movieverse.data.api

import dev.team08.movieverse.domain.model.FeedbackRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FeedbackApiService {
    @POST("api/feedback")
    suspend fun submitFeedback(@Body feedbackRequest: FeedbackRequest): Response<Void>
}
