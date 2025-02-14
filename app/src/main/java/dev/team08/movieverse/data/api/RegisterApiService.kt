package dev.team08.movieverse.data.api;

import dev.team08.movieverse.domain.model.RegisterRequest;
import dev.team08.movieverse.domain.model.RegisterResponse;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface RegisterApiService {
    @POST("api/auth/users/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}
