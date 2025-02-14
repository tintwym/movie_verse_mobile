package dev.team08.movieverse.data.api

import dev.team08.movieverse.common.Constants
import dev.team08.movieverse.domain.model.CastResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface CastApiService {
    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCast(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): CastResponse

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        val instance: CastApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CastApiService::class.java)
        }
    }
}
