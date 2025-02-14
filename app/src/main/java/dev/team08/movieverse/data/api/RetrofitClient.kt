package dev.team08.movieverse.data.api

import dev.team08.movieverse.common.Constants.BASE_URL
import dev.team08.movieverse.common.Constants.IMAGE_BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val AUTH_BASE_URL = "http://10.0.2.2:8080/"

    private val authRetrofit = Retrofit.Builder()
        .baseUrl(AUTH_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApiService: AuthApiService = authRetrofit.create(AuthApiService::class.java)
}