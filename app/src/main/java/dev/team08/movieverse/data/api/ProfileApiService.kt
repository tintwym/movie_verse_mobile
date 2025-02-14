package dev.team08.movieverse.data.api

import dev.team08.movieverse.domain.model.UpdateEmailRequest
import dev.team08.movieverse.domain.model.UpdateProfileResponse
import dev.team08.movieverse.domain.model.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface ProfileApiService {
    @GET("api/auth/profile/me")
    fun getUserProfile(
        @Header("Authorization") token: String
    ): Call<UserResponse>

    @PUT("api/auth/profile/update")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateEmailRequest
    ): Call<UpdateProfileResponse>
}
