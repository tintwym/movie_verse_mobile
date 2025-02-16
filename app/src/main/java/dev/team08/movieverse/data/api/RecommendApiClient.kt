package dev.team08.movieverse.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecommendApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val apiService: RecommendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecommendApiService::class.java)
    }
}
