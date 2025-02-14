package dev.team08.movieverse.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Movie(
    val id: Int,
    val title: String?,
    val overview: String?,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("vote_average")
    val voteAverage: Double?,

    @SerializedName("genre_ids")
    val genreIds: List<Int>?,

    val runtime: Int? = null,
    val genres: @RawValue List<Genre>? = null
) : Parcelable
