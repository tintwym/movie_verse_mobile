package dev.team08.movieverse.data.repository

import dev.team08.movieverse.data.api.RetrofitClient
import dev.team08.movieverse.domain.model.LoginRequest
import dev.team08.movieverse.domain.model.LoginResponse

class AuthRepository {
    private val apiService = RetrofitClient.authApiService

    suspend fun login(username: String, password: String): LoginResponse {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("Login failed: ${response.message()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }
}