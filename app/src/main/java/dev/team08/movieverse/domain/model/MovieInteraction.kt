package dev.team08.movieverse.domain.model

data class MovieInteraction(
    val createdAt: String,
    val updatedAt: String,
    val tmdbMovieId: Int,
    val views: Int,
    val likeStatus: String,
    val favorite: Boolean,
    val watchStatus: String
)
