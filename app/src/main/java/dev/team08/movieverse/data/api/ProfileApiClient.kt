package dev.team08.movieverse.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProfileApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val instance: ProfileApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProfileApiService::class.java)
    }
}
