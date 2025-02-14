package dev.team08.movieverse.data.api

import dev.team08.movieverse.common.Constants
import dev.team08.movieverse.domain.model.LoginRequest
import dev.team08.movieverse.domain.model.LoginResponse
import dev.team08.movieverse.domain.model.MovieResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
    @POST("api/auth/users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}
