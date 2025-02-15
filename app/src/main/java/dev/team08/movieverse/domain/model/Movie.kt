package dev.team08.movieverse.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Movie(
    val id: Int,
    val title: String? = null,
    val overview: String? = null,

    @SerializedName("poster_path")
    val posterPath: String? = null,

    @SerializedName("backdrop_path")
    val backdropPath: String? = null,

    @SerializedName("release_date")
    val releaseDate: String? = null,

    @SerializedName("vote_average")
    val voteAverage: Double? = null,

    @SerializedName("genre_ids")
    val genreIds: List<Int>? = null,

    val runtime: Int? = null,
    val genres: @RawValue List<Genre>? = null
) : Parcelable {
    fun getFullPosterPath(): String {
        return if (!posterPath.isNullOrBlank()) {
            "https://image.tmdb.org/t/p/w500${posterPath.trim()}"
        } else {
            ""
        }
    }

    fun getRating(): String {
        return if (voteAverage != null) {
            String.format("%.1f", voteAverage)
        } else {
            "N/A"
        }
    }
}
