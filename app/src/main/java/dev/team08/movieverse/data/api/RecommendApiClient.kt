package dev.team08.movieverse.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecommendApiClient {
    private const val BASE_URL = "http://localhost:8080/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: RecommendApiService = retrofit.create(RecommendApiService::class.java)
}
