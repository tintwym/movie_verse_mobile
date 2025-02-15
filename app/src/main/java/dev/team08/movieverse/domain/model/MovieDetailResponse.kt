package dev.team08.movieverse.domain.model

data class MovieDetailResponse(
    val id: Int,
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val runtime: Int?,
    val genres: List<Genre>?
)
